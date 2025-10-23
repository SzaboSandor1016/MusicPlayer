package com.example.features.player.presentation.fragment

import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.annotation.OptIn
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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.common.values.DEFAULT_ARTIST_NAMES
import com.example.core.ui.PlaylistDialogHelper
import com.example.core.ui.R
import com.example.core.ui.SongOptionsDialogHelper
import com.example.features.player.presentation.adapter.AdapterCurrentSourceRecyclerView
import com.example.features.player.presentation.databinding.FragmentPlayerBinding
import com.example.features.player.presentation.mappers.toSongMainPresentationModel
import com.example.features.player.presentation.model.MusicSourcePlayerPresentationModel
import com.example.features.player.presentation.model.PlaylistInfoPlayerPresentationModel
import com.example.features.player.presentation.model.SongPlayerPresentationModel
import com.example.features.player.presentation.service.PlaybackService
import com.example.features.player.presentation.viewmodel.ViewModelPlayer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/*private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"*/

/**
 * A simple [Fragment] subclass.
 * Use the [FragmentPlayer.newInstance] factory method to
 * create an instance of this fragment.
 */
class FragmentPlayer : Fragment() {
    // TODO: Rename and change types of parameters
    /*private var param1: String? = null
    private var param2: String? = null*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FragmentPlayer.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(/*param1: String, param2: String*/) =
            FragmentPlayer().apply {
                arguments = Bundle().apply {
                    /*putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)*/
                }
            }
    }

    private var sessionId: Int = 0
    private var isLargeLayout = false

    private var hasRestoredPrefs = false

    private var userSelected = false

    private val progressRefreshDelay = 1000L

    private var progressJob: Job? = null

    private var mediaController: MediaController? = null
    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val songs = mutableMapOf<Long, SongPlayerPresentationModel>()

    private val playlistsInfo: ArrayList<PlaylistInfoPlayerPresentationModel> = ArrayList()

    private lateinit var adapterSource: AdapterCurrentSourceRecyclerView

    private val viewModelPlayer: ViewModelPlayer by viewModel<ViewModelPlayer>( ownerProducer = { requireActivity() })

    private var _binding: FragmentPlayerBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            /*param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)*/
        }
    }

    override fun onStart() {
        super.onStart()

        val sessionToken = SessionToken(
            requireActivity().applicationContext,
            ComponentName(
                requireActivity().applicationContext,
                PlaybackService::class.java
            )
        )

        controllerFuture =
            MediaController.Builder(requireActivity().applicationContext,sessionToken).buildAsync()

        controllerFuture?.addListener({

            if (controllerFuture?.isDone == true) {

                mediaController = controllerFuture?.get()

                viewModelPlayer.isControllerCreated.value = true

                initController()

                if (!hasRestoredPrefs && (mediaController?.mediaItemCount?: 0) == 0) {

                    tryRestoreFromPreferences()
                    hasRestoredPrefs = true
                }

                restoreRepeatAndShuffleModeFromPreferences()

                reSyncControllerWithUI()
            }
        }, MoreExecutors.directExecutor())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(
            inflater,
            container,
            false
        )

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.songIcon.clipToOutline = true

        isLargeLayout = resources.getBoolean(com.example.core.ui.R.bool.large_layout)

        adapterSource = AdapterCurrentSourceRecyclerView()

        binding.playlistRecyclerView.layoutManager = LinearLayoutManager(
            requireContext(),
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
                        context = requireContext(),
                        playlists = playlistsInfo,
                        idSelector = { it.id },
                        labelSelector = { it.label },
                        onPlaylistSelected = { playlistId ->

                            viewModelPlayer.addToPlaylist(
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
                requireContext(),
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

                    Log.d("DragTest", "original index: $originalIndex");

                    selected = true
                }

                draggedIndex = viewHolder.bindingAdapterPosition

                Log.d("DragTest", "dragged index: $draggedIndex");

                targetIndex = target.bindingAdapterPosition

                Log.d("DragTest", "target index: $targetIndex");

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

                        Log.d("DragTest", "final target index: $targetIndex");

                        /*val adjustedTarget =
                            if (originalIndex < targetIndex) targetIndex - 1 else targetIndex*/

                        updateSourceFromReorder(originalIndex, targetIndex)
                    }
                    drag = false
                    selected = false
                }

            }

        })

        touchHelper.attachToRecyclerView(binding.playlistRecyclerView)

        /*lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                combine(
                    viewModelMain.isControllerCreated,
                    viewModelMain.musicSourceState
                ) { isCreated, source ->

                    isCreated to source

                }.filter{ it.first }.collect {(_, source) ->


                }
            }
        }*/

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                combine(
                    viewModelPlayer.isControllerCreated,
                    viewModelPlayer.musicSourceSharedFlow
                ) { isCreated, source ->

                    isCreated to source

                }.filter{ it.first }.collect {(_, source) ->

                    when (source) {
                        is MusicSourcePlayerPresentationModel.Default -> {
                            //updateSource(0, 0L, emptyList())
                        }

                        is MusicSourcePlayerPresentationModel.MusicSource -> {

                            updateSource(source)
                        }
                    }
                }
            }
        }

        /*lifecycleScope.launch {

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
        }*/

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.userSelected.collect {

                    userSelected = it
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.playlistInfoState.collect {

                    updatePlaylists(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.isCurrentFavorite.collect {

                    updateFavoriteButton(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.errorSharedFlow.collect {

                    showErrorToast(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                combine(
                    viewModelPlayer.isControllerCreated,
                    viewModelPlayer.enableShuffle
                ) { isCreated, shuffle ->
                    Pair(isCreated, shuffle)
                }.filter { it.first }.collect { (_, shuffle) ->

                    setShuffleFromSharedPreferences(shuffle)
                    //updateShuffleIndicator(shuffle)

                    Log.d("refresh_from_shared_prefs", "shuffle: $shuffle")
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                combine(
                    viewModelPlayer.isControllerCreated,
                    viewModelPlayer.repeatMode,
                ) { isCreated, repeatMode ->
                    Pair(isCreated, repeatMode)
                }.filter { it.first }.collect { (_, repeatMode) ->

                    setRepeatModeFromSharedPrefs(repeatMode)
                    //updateRepeatModeIndicator(repeatMode)

                    Log.d("refresh_from_shared_prefs", "repeatMode: $repeatMode")
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.addedUpNext.collect {

                    addSongToUpNext(it)
                }
            }
        }

        lifecycleScope.launch {

            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModelPlayer.addedQueued.collect {

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
    }

    override fun onStop() {

        stopProgressLoop()

        mediaController?.release()

        controllerFuture?.let { MediaController.releaseFuture(it) }

        mediaController = null

        viewModelPlayer.isControllerCreated.value = false

        super.onStop()
    }

    @OptIn(UnstableApi::class)
    private fun initController() {

        mediaController?.addListener(object: Player.Listener {

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                mediaItem?.let {
                    viewModelPlayer.checkIfSongIsContainedInFavorites(mediaItem.mediaId.toLong())

                    val isUpNext = viewModelPlayer.checkIfUpNextContains(mediaItem.mediaId.toLong())

                    if (isUpNext) {
                        viewModelPlayer.removeFromUpNext(listOf(mediaItem.mediaId.toLong()))
                    }
                }

                //setCurrentIndexPreference()

                syncControllerSongsWithRecyclerView()

                updateCurrentSongDetails(mediaItem)
            }

            override fun onAudioSessionIdChanged(audioSessionId: Int) {
                super.onAudioSessionIdChanged(audioSessionId)

                if (audioSessionId == C.AUDIO_SESSION_ID_UNSET) return

                sessionId = audioSessionId
                viewModelPlayer.onStart(audioSessionId)
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
                viewModelPlayer.onStart(sessionId)
            }

            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                super.onTimelineChanged(timeline, reason)

                setMediaIdsPreference()

                syncControllerSongsWithRecyclerView()
            }

            override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onPlaylistMetadataChanged(mediaMetadata)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)

                updateShuffleIndicator(shuffleModeEnabled)
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)

                updateRepeatModeIndicator(repeatMode)

                Log.d("repeatMode_callback", repeatMode.toString())
            }
        })
    }

    private fun tryRestoreFromPreferences() {

        viewModelPlayer.restoreFromPreferences()
    }

    private fun restoreRepeatAndShuffleModeFromPreferences() {

        viewModelPlayer.restoreRepeatAndShuffleFromPreferences()
    }

    private fun addRemoveFavorite() {

        mediaController?.currentMediaItem?.let {

            Log.d("favorite_selected_activity", it.mediaId)

            viewModelPlayer.addRemoveFavorite(it.mediaId.toLong())
        }
    }

    private fun addSongToUpNext(mediaItem: MediaItem) {

        mediaController?.let { controller ->

            val mediaItems = getMediaItems()

            val currentIndex = controller.currentMediaItemIndex

            val lastUpNextId = viewModelPlayer.getLastIdOfUpNexts()

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

            viewModelPlayer.addToUpNext(mediaItem.mediaId.toLong())

            setMediaIdsPreference()
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

        viewModelPlayer.setMediaIdsPreference(mediaItems)
    }

    private fun setCurrentIndexPreference() {

        val index = mediaController?.currentMediaItemIndex

        index?.let { viewModelPlayer.setCurrentIndexPreference(it) }
    }

    private fun updateSource(musicSource: MusicSourcePlayerPresentationModel.MusicSource) {

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
                setMediaIdsPreference()
            }

            //controller.playWhenReady = userSelected

            updateCurrentSongDetails(controller.currentMediaItem)

            userSelected = false
        }
    }

    fun updateSourceFromReorder(currentIndex: Int, targetIndex: Int) {

        mediaController?.moveMediaItem(currentIndex, targetIndex)

        //syncControllerSongsWithRecyclerView()
        //setMediaIdsPreference()
    }

    private fun reSyncControllerWithUI() {

        mediaController?.let { controller ->

            updateCurrentSongDetails(controller.currentMediaItem)

            handlePlayingChanged(controller.isPlaying)

            syncControllerSongsWithRecyclerView()
        }
    }

    private fun syncControllerSongsWithRecyclerView() {

        //val timeline = mediaController?.currentTimeline

        val currentIndex =  mediaController?.currentMediaItemIndex

        val upNextIndexes = checkUpNextItems(currentIndex)

        val mediaItems = getMediaItems().mapIndexed { index, item ->

            item.toSongMainPresentationModel(
                index == currentIndex,
                index in upNextIndexes
            )
        }

        updateSongs(mediaItems)
    }

    fun checkUpNextItems(currentMediaItemIndex: Int?): List<Int> {

        if (currentMediaItemIndex == null) return emptyList()

        val mediaItems = getMediaItems().map { it.mediaId.toLong() }

        val upNextIds = viewModelPlayer.getUpNextIds()

        if (mediaItems.isEmpty() || upNextIds.isEmpty()) return emptyList()

        val idsWithIndexes = upNextIds.associateWith {
            mediaItems.indexOf(it)
        }

        return if ((currentMediaItemIndex + 1) in idsWithIndexes.values) {

            val remaining = idsWithIndexes.filter { it.value > currentMediaItemIndex }

            val removed = idsWithIndexes.filter { it.value !in remaining.values }

            viewModelPlayer.removeFromUpNext(removed.keys.toList())

            remaining.values.toList()
        }else {

            viewModelPlayer.removeFromUpNext(upNextIds)

            emptyList()
        }
    }

    fun updateCurrentSongDetails(mediaItem: MediaItem?) {

        mediaItem?.mediaMetadata?.let {

            binding.songTitle.text = it.title

            val artist = if(it.artist in DEFAULT_ARTIST_NAMES)
                resources.getString(com.example.core.ui.R.string.unknown_artist)
            else it.artist

            binding.songArtist.text = artist

            val duration = it.durationMs?.toInt()?.div(1000)?: 0

            binding.seekBar.max = duration

            val progressString = "00:00/"+ getTimeString(duration)

            binding.progress.setText(progressString)

            Glide.with(this)
                .load(it.artworkUri)
                .placeholder(R.drawable.ic_music_note_single_36)
                .error(R.drawable.ic_music_note_single_36)
                .into(binding.songIcon)
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

    private fun updateSongs(songs: List<SongPlayerPresentationModel>) {

        /*this.songs.clear()

        this.songs.addAll(songs)*/

        this.adapterSource.submitList(songs.toList())

        this.adapterSource.notifyDataSetChanged()
    }

    private fun updatePlaylists(playlists: List<PlaylistInfoPlayerPresentationModel>) {

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

    private fun setShuffleFromSharedPreferences(shuffle: Boolean) {

        shuffle.let {

            mediaController?.shuffleModeEnabled = it
        }
    }

    private fun updateShuffleIndicator(shuffle: Boolean) {

        binding.shuffle.isChecked = shuffle
    }

    private fun setRepeatMode() {

        val repeatMode = mediaController?.repeatMode

        Log.d("repeatMode_controller", "repeatMode: $repeatMode")

        when (repeatMode) {
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
                //mediaController?.repeatMode = Player.REPEAT_MODE_OFF
            }
        }
    }

    private fun setRepeatModeFromSharedPrefs(repeatMode: Int) {

        repeatMode.let {
            mediaController?.repeatMode = it
        }

        Log.d("repeatMode_player", mediaController?.repeatMode.toString())
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

    private fun updateFavoriteButton(isFavorite: Boolean) {

        binding.favourite.isChecked = isFavorite
    }

    private fun getMediaItems(): List<MediaItem> {

        val mediaItems = mediaController?.currentTimeline?.let { timeline ->

            val items = mutableListOf<MediaItem>()

            for (i in 0 until timeline.windowCount) {

                val window = Timeline.Window()

                timeline.getWindow(i, window)

                items.add(window.mediaItem)
            }

            items
        }?: emptyList<MediaItem>()

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

        val fragmentManager = parentFragmentManager
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

            transaction.setReorderingAllowed(true)
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