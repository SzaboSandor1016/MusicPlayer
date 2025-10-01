package com.example.musicplayer

import android.Manifest
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core.common.values.FAVORITES_ID
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.SongOptionsDialogHelper
import com.example.musicplayer.adapter.AdapterCurrentSourceRecyclerView
import com.example.musicplayer.databinding.ActivityMainBinding
import com.example.musicplayer.mappers.toSongMainPresentationModel
import com.example.musicplayer.models.MusicSourceMainPresentationModel
import com.example.musicplayer.models.PlaylistInfoMainPresentationModel
import com.example.musicplayer.models.SongMainPresentationModel
import com.example.musicplayer.service.PlaybackService
import com.example.musicplayer.viewmodel.ViewModelMain
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private var sessionId: Int = 0
    private var isLargeLayout = false

    private var hasRestoredPrefs = false

    private var userSelected = false

    private val progressRefreshDelay = 1000L

    private var progressJob: Job? = null

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val songs: ArrayList<SongMainPresentationModel> = ArrayList()

    private val playlistsInfo: ArrayList<PlaylistInfoMainPresentationModel> = ArrayList()

    private lateinit var adapterSource: AdapterCurrentSourceRecyclerView

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    private val viewModelMain: ViewModelMain by inject<ViewModelMain>()

    private lateinit var binding: ActivityMainBinding

    companion object {

        private const val PERMISSIONS_REQUEST_READ_MEDIA_AUDIO = 1
    }

    override fun onStart() {
        super.onStart()

        val sessionToken = SessionToken(
            this,
            ComponentName(
                this,
                PlaybackService::class.java
            )
        )

        controllerFuture =
            MediaController.Builder(this,sessionToken).buildAsync()

        controllerFuture?.addListener({

            if (controllerFuture?.isDone == true) {

                mediaController = controllerFuture?.get()

                viewModelMain.isControllerCreated.value = true

                initController()

                if (!hasRestoredPrefs && (mediaController?.mediaItemCount?: 0) == 0) {

                    tryRestoreFromPreferences()
                    hasRestoredPrefs = true
                }

                reSyncControllerWithUI()
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_READ_MEDIA_AUDIO
            )
        }

        isLargeLayout = resources.getBoolean(com.example.core.ui.R.bool.large_layout)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetContainer)

        bottomSheetBehavior.isHideable = false

        bottomSheetBehavior.isFitToContents = true

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {

            override fun onTabSelected(p0: TabLayout.Tab?) {
                val index = p0?.position

                if (index != null) {

                    handleTabSelect(
                        index = index
                    )
                    viewModelMain.selectTab(
                        index = index
                    )
                }
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

        })

        adapterSource = AdapterCurrentSourceRecyclerView()

        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        binding.playlistRecyclerView.adapter = adapterSource

        adapterSource.setOnClickListener { songId ->

            val mediaItems = getMediaItems()

            val selected = mediaItems.first { it.mediaId.toLong() == songId }

            val index = mediaItems.indexOf(selected)

            mediaController?.seekTo(index, 0L)
        }

        adapterSource.setOnMoreOptionsClickListener { view, songId ->

            val options = mutableListOf<Pair<Int, () -> Unit>>()

            options.addAll(listOf(
                com.example.core.ui.R.string.add_to_playlist to {

                    PlaylistDialogHelper.showPlaylistSelectDialog(
                        com.example.core.ui.R.string.add_to_playlist,
                        com.example.core.ui.R.string.choose_playlist,
                        context = this@MainActivity,
                        playlists = playlistsInfo,
                        idSelector = { it.id },
                        labelSelector = { it.label },
                        onPlaylistSelected = { playlistId ->

                            viewModelMain.addToPlaylist(
                                playlistId,
                                songId
                            )
                        }
                    )
                },
                com.example.core.ui.R.string.add_to_next_up to {

                    getMediaItems().firstOrNull { it.mediaId.toLong() == songId }?.let {
                        addSongToUpNext(it)
                    }
                },
                com.example.core.ui.R.string.remove_from_queue to {

                    val index = getMediaItems().indexOfFirst { it.mediaId.toLong() == songId }

                    if (index != -1) {
                        removeSongFromQueue(index)
                    }
                }
            ))

            SongOptionsDialogHelper.showSongOptionsDialog(
                this@MainActivity,
                view,
                options
            )
        }

        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {

            var drag = false
            var selected = false

            var draggedIndex: Int = 0
            var targetIndex: Int = 0

            var originalIndex: Int = 0

            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {

                return makeMovementFlags(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    ItemTouchHelper.END
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

                if (!selected) {
                    originalIndex = viewHolder.bindingAdapterPosition

                    selected = true
                }

                draggedIndex = viewHolder.bindingAdapterPosition
                targetIndex = target.bindingAdapterPosition

                adapterSource.notifyItemMoved(draggedIndex, targetIndex);

                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int
            ) {
                /*when (direction) {
                    ItemTouchHelper.END -> {
                        val removed = songs[viewHolder.bindingAdapterPosition]
                        viewModelRoute.removePlaceFromRoute(removed.placeUUID)
                    }
                }*/
            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    drag = true
                    Log.d("DragTest", "DRAGGGING start");
                }
                if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && drag) {
                    Log.d("DragTest", "DRAGGGING stop");

                    if (draggedIndex != targetIndex) {

                        updateSourceFromReorder(originalIndex, targetIndex)
                    }
                    drag = false
                    selected = false
                }

            }

        })

        touchHelper.attachToRecyclerView(binding.playlistRecyclerView)

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                combine(
                    viewModelMain.isControllerCreated,
                    viewModelMain.musicSourceState
                ) { isCreated, source ->

                    isCreated to source

                }.filter{ it.first }.collect {(_, source) ->

                    when (source) {
                        is MusicSourceMainPresentationModel.Default -> {
                            //updateSource(0, 0L, emptyList())
                        }

                        is MusicSourceMainPresentationModel.MusicSource -> {

                            updateSource(source)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.restorePrefsSharedFlow.collect {

                    updateSource(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.userSelected.collect {

                    userSelected = it
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.selectedTabIndexState.collect {

                    if (it != binding.tabLayout.selectedTabPosition) {

                        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(it))
                    }
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.playlistInfoState.collect {

                    updatePlaylists(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.isCurrentFavorite.collect {

                    updateFavoriteButton(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.errorSharedFlow.collect {

                    showErrorToast(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                combine(
                    viewModelMain.isControllerCreated,
                    viewModelMain.repeatMode,
                    viewModelMain.enableShuffle
                ) { isCreated, repeatMode, shuffle ->
                    Triple(isCreated, repeatMode, shuffle)
                }.filter { it.first }.collect { (_, repeatMode, shuffle) ->

                    setRepeatModeFromSharedPrefs(repeatMode)
                    updateRepeatModeIndicator(repeatMode)

                    setShuffle(shuffle)
                    updateShuffleIndicator(shuffle)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.addedUpNext.collect {

                    addSongToUpNext(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelMain.addedQueued.collect {

                    addSongToQueue(it)
                }
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                if (fromUser) {

                    mediaController?.seekTo(progress.toLong() * 1000)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

                stopProgressLoop()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

                startProgressLoop()
            }
        })

        binding.playPause.setOnClickListener { l ->

            if (mediaController?.isPlaying == true) {

                pause()
            } else {

                play()
            }
        }

        binding.skipNext.setOnClickListener { l ->

            skipNext()
        }

        binding.skipPrevious.setOnClickListener { l ->

            skipPrevious()
        }

        binding.repeatMode.setOnClickListener { l ->

            setRepeatMode()
        }

        binding.shuffle.setOnClickListener { l ->

            setShuffle()
        }

        binding.equalizer.setOnClickListener { l ->
            showEqualizerDialog()
        }

        binding.favourite.setOnClickListener { l ->

            addRemoveFavorite()

            binding.favourite.isChecked.let {

                !it
            }
        }

        //binding.playerView.setPlayer(mediaController)
    }

    override fun onStop() {

        stopProgressLoop()

        mediaController?.release()

        controllerFuture?.let { MediaController.releaseFuture(it) }

        mediaController = null

        viewModelMain.isControllerCreated.value = false

        super.onStop()
    }

    private fun handleTabSelect(index: Int) {

        val request = when(index) {

            0 -> NavDeepLinkRequest.Builder.fromUri(
            "android-app://features/songs".toUri()
            ).build()
            else -> NavDeepLinkRequest.Builder.fromUri(
                "android-app://features/playlists".toUri()
            ).build()
        }

        findNavController(R.id.nav_host_fragment_content_main).navigate(request)
    }

    @OptIn(UnstableApi::class)
    private fun initController() {

        mediaController?.addListener(object: Player.Listener {

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                mediaItem?.let {
                    viewModelMain.checkIfSongIsContainedInFavorites(mediaItem.mediaId.toLong())

                    val isUpNext = viewModelMain.checkIfUpNextContains(mediaItem.mediaId.toLong())

                    if (isUpNext) {
                        viewModelMain.removeFromUpNext(listOf(mediaItem.mediaId.toLong()))
                    }
                }

                setCurrentIndexPreference()

                syncControllerSongsWithRecyclerView(mediaController?.currentTimeline)

                updateTitleSeekBarAndProgressString(mediaItem)
            }

            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                super.onAudioSessionIdChanged(audioSessionId)

                if (audioSessionId == C.AUDIO_SESSION_ID_UNSET) return

                sessionId = audioSessionId
                viewModelMain.onStart(audioSessionId)
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)

                val currentPosition =
                    mediaController?.currentPosition?.toInt()?.div(1000)
                        ?: (newPosition.positionMs.toInt() / 1000)

                val playbackDuration =
                    mediaController?.currentMediaItem?.mediaMetadata?.durationMs?.toInt()?.div(1000)?: 0

                binding.seekBar.progress = currentPosition

                val progressString = getTimeString(currentPosition) + "/" + getTimeString(playbackDuration)

                binding.progress.setText(progressString)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)

                handlePlayingChanged(isPlaying)

                if (sessionId == C.AUDIO_SESSION_ID_UNSET) return
                viewModelMain.onStart(sessionId)
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)

                setMediaIdsPreference()

                syncControllerSongsWithRecyclerView(timeline)
            }

            override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onPlaylistMetadataChanged(mediaMetadata)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)

                viewModelMain.enableDisableShuffle(shuffleModeEnabled)

                updateShuffleIndicator(shuffleModeEnabled)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)

                viewModelMain.setRepeatMode(repeatMode)

                updateRepeatModeIndicator(repeatMode)

                Log.d("repeatMode_callback", repeatMode.toString())
            }
        })
    }

    private fun tryRestoreFromPreferences() {

        viewModelMain.restoreFromPreferences()
    }

    private fun addRemoveFavorite() {

        mediaController?.currentMediaItem?.let {

            viewModelMain.addRemoveFavorite(it.mediaId.toLong())
        }
    }

    private fun addSongToUpNext(mediaItem: MediaItem) {

        mediaController?.let { controller ->

            val mediaItems = getMediaItems()

            val currentIndex = controller.currentMediaItemIndex

            val lastUpNextId = viewModelMain.getLastIdOfUpNexts()

            val lastUpNextIndex = lastUpNextId?.let {
                mediaItems.indexOfFirst { it.mediaId.toLong() == lastUpNextId }
            } ?: currentIndex

            val existingIndex = mediaItems.indexOfFirst { it.mediaId == mediaItem.mediaId }

            if (existingIndex != -1) {

                controller.moveMediaItem(existingIndex, lastUpNextIndex + 1)

            } else {

                controller.addMediaItem(
                    lastUpNextIndex + 1,
                    mediaItem
                )
            }

            viewModelMain.addToUpNext(mediaItem.mediaId.toLong())
        }
    }

    private fun addSongToQueue(mediaItem: MediaItem) {

        val size = getMediaItems().size

        mediaController?.let {

            it.addMediaItem(size, mediaItem)

            setMediaIdsPreference()

            setCurrentIndexPreference()
        }
    }

    private fun removeSongFromQueue(indexOfRemoved: Int) {

        mediaController?.let { controller ->

            if (controller.currentMediaItemIndex != indexOfRemoved)
                controller.removeMediaItem(indexOfRemoved)

            setMediaIdsPreference()

            setCurrentIndexPreference()
        }
    }

    private fun setMediaIdsPreference() {
        val mediaItems = getMediaItems().map { it.mediaId.toLong() }.toSet()

        viewModelMain.setMediaIdsPreference(mediaItems)
    }

    private fun setCurrentIndexPreference() {

        val index = mediaController?.currentMediaItemIndex

        index?.let { viewModelMain.setCurrentIndexPreference(it) }
    }

    private fun updateSource(musicSource: MusicSourceMainPresentationModel.MusicSource) {

        mediaController?.let { controller ->

            val newIds = musicSource.songs.map { it.mediaId }
            val currentIds = getMediaItems().map { it.mediaId }

            val shouldUpdate =
                newIds != currentIds || userSelected

            if (shouldUpdate) {

                try {
                    controller.setMediaItems(
                        musicSource.songs,
                        musicSource.selectedIndex,
                        musicSource.position
                    )
                } catch (e: Exception) {
                    controller.setMediaItems(
                        musicSource.songs,
                        0,
                        0
                    )
                }
            }

            //controller.playWhenReady = userSelected

            updateTitleSeekBarAndProgressString(controller.currentMediaItem)

            userSelected = false
        }
    }

    fun updateSourceFromReorder(currentIndex: Int, targetIndex: Int) {

        if (mediaController?.currentMediaItemIndex == targetIndex) return

        mediaController?.moveMediaItem(currentIndex, targetIndex)
    }

    private fun reSyncControllerWithUI() {

        mediaController?.let { controller ->

            updateTitleSeekBarAndProgressString(controller.currentMediaItem)

            handlePlayingChanged(controller.isPlaying)

            syncControllerSongsWithRecyclerView(controller.currentTimeline)
        }
    }

    private fun syncControllerSongsWithRecyclerView(timeline: Timeline?) {

        //val timeline = mediaController?.currentTimeline

        val currentIndex =  mediaController?.currentMediaItemIndex

        val upNextIndexes = checkUpNextItems(currentIndex)

        timeline?.let { timeline ->
            val newPlaylist = mutableListOf<SongMainPresentationModel>()

            for (i in 0 until timeline.windowCount) {

                val window = Timeline.Window()

                timeline.getWindow(i, window)

                val item = window.mediaItem

                newPlaylist.add(
                    item.toSongMainPresentationModel(
                        i == currentIndex,
                        i in upNextIndexes
                    )
                )
            }

            updateSongs(newPlaylist)
        }
    }

    fun checkUpNextItems(currentMediaItemIndex: Int?): List<Int> {

        if (currentMediaItemIndex == null) return emptyList()

        val mediaItems = getMediaItems().map { it.mediaId.toLong() }

        val upNextIds = viewModelMain.getUpNextIds()

        if (mediaItems.isEmpty() || upNextIds.isEmpty()) return emptyList()

        val idsWithIndexes = upNextIds.associateWith {
            mediaItems.indexOf(it)
        }

        return if ((currentMediaItemIndex + 1) in idsWithIndexes.values) {

            val remaining = idsWithIndexes.filter { it.value > currentMediaItemIndex }

            val removed = idsWithIndexes.filter { it.value !in remaining.values }

            viewModelMain.removeFromUpNext(removed.keys.toList())

            remaining.values.toList()
        }else {

            viewModelMain.removeFromUpNext(upNextIds)

            emptyList()
        }
    }

    fun updateTitleSeekBarAndProgressString(mediaItem: MediaItem?) {

        mediaItem?.mediaMetadata?.let {
            binding.songTitle.text = it.title

            val duration = it.durationMs?.toInt()?.div(1000)?: 0

            binding.seekBar.max = duration

            val progressString = "00:00/"+ getTimeString(duration)

            binding.progress.setText(progressString)
        }
    }

    private fun handlePlayingChanged(isPlaying: Boolean) {

        if (!isPlaying) {

            stopProgressLoop()

            binding.playPause.setIconResource(com.example.core.ui.R.drawable.ic_play_24)
        } else {

            startProgressLoop()

            binding.playPause.setIconResource(com.example.core.ui.R.drawable.ic_pause_24)
        }
    }

    private fun updateSongs(songs: List<SongMainPresentationModel>) {

        this.songs.clear()

        this.songs.addAll(songs)

        this.adapterSource.submitList(this.songs.toList())
    }

    private fun updatePlaylists(playlists: List<PlaylistInfoMainPresentationModel>) {

        this.playlistsInfo.clear()

        this.playlistsInfo.addAll(playlists)
    }

    private fun startProgressLoop() {

        progressJob?.cancel()

        progressJob = lifecycleScope.launch {

            var duration: Int = 0

            mediaController?.let { controller ->

                duration = controller.currentMediaItem?.mediaMetadata?.durationMs?.toInt()?.div(1000)?: 0

                binding.seekBar.max = duration
            }



            /*if (songs.isNotEmpty()) {

                syncControllerSongsWithUI()
            }*/

            while(isActive) {

                val currentPositionMs = mediaController?.currentPosition?: 0

                val currentPosition = currentPositionMs.toInt().div(1000)

                viewModelMain.setPositionPreference(currentPositionMs)

                binding.seekBar.progress = currentPosition

                val progressString =
                    getTimeString(currentPosition) + "/" + getTimeString(duration)

                binding.progress.setText(progressString)

                delay(progressRefreshDelay)
            }
        }
    }

    private fun stopProgressLoop() {
        progressJob?.cancel()
        progressJob = null
    }

    private fun play() {

        if (mediaController?.playbackState == Player.STATE_IDLE)
            mediaController?.prepare()

        mediaController?.play()
    }

    private fun pause() {

        mediaController?.pause()
    }

    private fun skipNext() {

        mediaController?.seekToNext()
    }

    private fun skipPrevious() {

        mediaController?.seekToPrevious()
    }

    private fun setShuffle() {

        mediaController?.shuffleModeEnabled?.let { mediaController?.shuffleModeEnabled = !it }
    }

    private fun setShuffle(shuffle: Boolean) {

        mediaController?.shuffleModeEnabled = shuffle
    }

    private fun updateShuffleIndicator(shuffle: Boolean) {

        binding.shuffle.isChecked = shuffle
    }

    private fun setRepeatMode() {

        mediaController?.repeatMode?.let {
            when (it) {
                Player.REPEAT_MODE_OFF -> {
                    //viewModelMain.setRepeatMode(Player.REPEAT_MODE_ALL)
                    mediaController?.repeatMode = Player.REPEAT_MODE_ALL
                }
                Player.REPEAT_MODE_ALL -> {
                    //viewModelMain.setRepeatMode(Player.REPEAT_MODE_ONE)
                    mediaController?.repeatMode = Player.REPEAT_MODE_ONE
                }
                Player.REPEAT_MODE_ONE -> {
                    //viewModelMain.setRepeatMode(Player.REPEAT_MODE_OFF)
                    mediaController?.repeatMode = Player.REPEAT_MODE_OFF
                }
                else -> {
                    //viewModelMain.setRepeatMode(Player.REPEAT_MODE_OFF)
                    mediaController?.repeatMode = Player.REPEAT_MODE_OFF
                }
            }
        }
    }

    private fun updateRepeatModeIndicator(repeatMode: Int) {

        when(repeatMode) {

            Player.REPEAT_MODE_OFF -> {

                binding.repeatMode.isChecked = false

                binding.repeatMode.setIconResource(com.example.core.ui.R.drawable.ic_repeat_24)
            }

            Player.REPEAT_MODE_ALL -> {

                binding.repeatMode.isChecked = true

                binding.repeatMode.setIconResource(com.example.core.ui.R.drawable.ic_repeat_24)
            }

            Player.REPEAT_MODE_ONE -> {

                binding.repeatMode.isChecked = true

                binding.repeatMode.setIconResource(com.example.core.ui.R.drawable.ic_repeat_one_24)
            }
        }
    }

    private fun setRepeatModeFromSharedPrefs(repeatMode: Int) {

        mediaController?.let {
            when (repeatMode) {
                Player.REPEAT_MODE_OFF -> it.repeatMode = Player.REPEAT_MODE_OFF
                Player.REPEAT_MODE_ALL -> it.repeatMode = Player.REPEAT_MODE_ALL
                Player.REPEAT_MODE_ONE -> it.repeatMode = Player.REPEAT_MODE_ONE
                else -> it.repeatMode = Player.REPEAT_MODE_OFF
            }
        }

        Log.d("repeatMode_player", mediaController?.repeatMode.toString())
    }

    private fun updateFavoriteButton(isFavorite: Boolean) {

        binding.favourite.isChecked = isFavorite
    }

    private fun getMediaItems(): List<MediaItem> {

        val mediaItems = (0 until (mediaController?.mediaItemCount?: 0))
            .mapNotNull { index -> mediaController?.takeIf { index in 0 until (it.mediaItemCount) }?.getMediaItemAt(index) }

        return mediaItems
    }

    private fun getTimeString(progress: Int): String {

        val minutes = progress / 60

        val seconds = progress % 60

        return String.format("%02d:%02d", minutes, seconds)
    }

    private fun getCurrentSongDurationInMs(): Long {

        return mediaController?.duration?: 0
    }


    private fun showEqualizerDialog() {

        val fragmentManager = supportFragmentManager
        val newFragment = FragmentEqualizer()
        if (isLargeLayout) {
            // The device is using a large layout, so show the fragment as a
            // dialog.
            newFragment.show(fragmentManager, "dialog")
        } else {
            // The device is smaller, so show the fragment fullscreen.
            val transaction = fragmentManager.beginTransaction()
            // For a polished look, specify a transition animation.
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity.
            transaction
                .add(android.R.id.content, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun showErrorToast(errorCode: Int) {

        val message = when(errorCode) {

            0 -> com.example.core.ui.R.string.song_already_contained
            else -> com.example.core.ui.R.string.nothing
        }

        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}