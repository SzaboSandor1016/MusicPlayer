package com.example.features.player.presentation.viewmodel

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import com.example.core.common.values.AUTO_PLAYLIST_IDS
import com.example.core.common.values.FAVORITES_ID
import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.musicsource.domain.usecases.GetAddQueuedUseCase
import com.example.features.musicsource.domain.usecases.GetAddUpNextUseCase
import com.example.features.musicsource.domain.usecases.GetMusicSourceUseCase
import com.example.features.player.presentation.mappers.toMusicSourceMainPresentationModel
import com.example.features.player.presentation.mappers.toPlaylistInfoMainPresentationModel
import com.example.features.player.presentation.mappers.toSongIDMainPresentationModel
import com.example.features.player.presentation.model.AudioEffectPlayerPresentationModel
import com.example.features.player.presentation.model.BassBoostVirtualizerEffectPlayerUIModel
import com.example.features.player.presentation.model.MusicSourcePlayerPresentationModel
import com.example.features.player.presentation.model.PlaylistInfoPlayerPresentationModel
import com.example.features.player.presentation.model.SongIDPlayerPresentationModel
import com.example.features.player.presentation.sharedprefs.BassBoostVirtualizerPreferences
import com.example.features.player.presentation.sharedprefs.EqualizerPreferences
import com.example.features.player.presentation.sharedprefs.PlayerPreferences
import com.example.features.player.presentation.values.ACOUSTIC
import com.example.features.player.presentation.values.DANCE
import com.example.features.player.presentation.values.FLAT
import com.example.features.player.presentation.values.HIP_HOPE
import com.example.features.player.presentation.values.JAZZ
import com.example.features.player.presentation.values.PODCAST
import com.example.features.player.presentation.values.POP
import com.example.features.player.presentation.values.PRESET_ACOUSTIC
import com.example.features.player.presentation.values.PRESET_CUSTOM
import com.example.features.player.presentation.values.PRESET_DANCE_LOUNGE
import com.example.features.player.presentation.values.PRESET_FLAT
import com.example.features.player.presentation.values.PRESET_HIP_HOP
import com.example.features.player.presentation.values.PRESET_JAZZ_BLUES
import com.example.features.player.presentation.values.PRESET_PODCAST
import com.example.features.player.presentation.values.PRESET_POP
import com.example.features.player.presentation.values.PRESET_ROCK
import com.example.features.player.presentation.values.ROCK
import com.example.features.playlists.domain.usecases.CheckIsSongContainedInPlaylistUseCase
import com.example.features.playlists.domain.usecases.DeletePlaylistSongUseCase
import com.example.features.playlists.domain.usecases.GetAllPlaylistsFromRoomUseCase
import com.example.features.playlists.domain.usecases.InsertPlaylistSongUseCase
import com.example.features.songs.domain.usecase.AssembleSourceMediaItemsFlowUseCase
import com.example.features.songs.domain.usecase.GetAllSongsFromRoomUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelPlayer(
    private val equalizerPreferences: EqualizerPreferences,
    private val playerPreferences: PlayerPreferences,
    private val bassBoostVirtualizerPreferences: BassBoostVirtualizerPreferences,
    private val getAddUpNextUseCase: GetAddUpNextUseCase,
    private val getAddQueuedUseCase: GetAddQueuedUseCase,
    private val getMusicSourceUseCase: GetMusicSourceUseCase,
    private val assembleSourceMediaItemsFlowUseCase: AssembleSourceMediaItemsFlowUseCase,
    private val getAllPlaylistsFromRoomUseCase: GetAllPlaylistsFromRoomUseCase,
    private val insertPlaylistSongUseCase: InsertPlaylistSongUseCase,
    private val deletePlaylistSongUseCase: DeletePlaylistSongUseCase,
    private val checkIsSongContainedInPlaylistUseCase: CheckIsSongContainedInPlaylistUseCase,
    private val getAllSongsFromRoomUseCase: GetAllSongsFromRoomUseCase
): ViewModel() {

    // MutableStateFlow to observe and emit changes in audio effects
    val audioEffects = MutableStateFlow<AudioEffectPlayerPresentationModel?>(null)

    val bassBoostEffects = MutableStateFlow<BassBoostVirtualizerEffectPlayerUIModel?>(null)

    // Instance of the Equalizer class from the Android system library
    private var equalizer: Equalizer? = null

    private var bassBoost: BassBoost? = null

    private var virtualizer: Virtualizer? = null

    // MutableStateFlow to observe and emit changes in the equalizer's enable/disable state
    val enableEqualizer = MutableStateFlow(false)

    // Unique audio session ID associated with the Exoplayer
    private var audioSessionId = 0

    private val _musicSourceSharedFlow = MutableSharedFlow<MusicSourcePlayerPresentationModel>()

    val musicSourceSharedFlow = _musicSourceSharedFlow.asSharedFlow()

    val isControllerCreated: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private val _enableShuffle = MutableSharedFlow<Boolean>()
    val enableShuffle = _enableShuffle.asSharedFlow()
    private val _repeatMode = MutableSharedFlow<Int>()
    val repeatMode = _repeatMode.asSharedFlow()

    private val _userSelected: MutableSharedFlow<Boolean> = MutableSharedFlow()

    val userSelected: SharedFlow<Boolean> = _userSelected.asSharedFlow()

    private val _isCurrentFavorite = MutableSharedFlow<Boolean>()

    val isCurrentFavorite = _isCurrentFavorite.asSharedFlow()

    private val _allSongsState: MutableStateFlow<List<SongIDPlayerPresentationModel>> = MutableStateFlow(emptyList())
    val allSongsState: StateFlow<List<SongIDPlayerPresentationModel>> = _allSongsState.asStateFlow()

    val playlistInfoState: StateFlow<List<PlaylistInfoPlayerPresentationModel>> by lazy {

        getAllPlaylistsFromRoomUseCase().map { playlists ->

            playlists.filter { it.id !in AUTO_PLAYLIST_IDS }.map {
                it.toPlaylistInfoMainPresentationModel()
            }
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    /*val musicSourceState: StateFlow<MusicSourceMainPresentationModel> by lazy {

        getMusicSourceUseCase().map { source ->

            _userSelected.emit(true)

            when (source) {
                is MusicSourceMusicSourceDomainModel.None -> {

                    MusicSourceMainPresentationModel.Default
                }

                is MusicSourceMusicSourceDomainModel.Source -> {

                    val mediaItems = assembleSourceMediaItemsFlowUseCase(source.songs.toList())

                    source.toMusicSourceMainPresentationModel(mediaItems)
                }
            }

        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            MusicSourceMainPresentationModel.Default
        )
    }*/

    private val _upNextState: MutableStateFlow<List<Long>> = MutableStateFlow(emptyList())

    private val _addedUpNext: MutableSharedFlow<MediaItem> = MutableSharedFlow()

    val addedUpNext = _addedUpNext.asSharedFlow()

    private val _addedQueued: MutableSharedFlow<MediaItem> = MutableSharedFlow()

    val addedQueued = _addedQueued.asSharedFlow()

    private val _errorSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()

    val errorSharedFlow: SharedFlow<Int> = _errorSharedFlow.asSharedFlow()

    init {
        // Retrieve and set the initial equalizer enable/disable state and audio effects from preferences
        enableEqualizer.value = equalizerPreferences.isEqualizerEnabled
        audioEffects.tryEmit(equalizerPreferences.audioEffects)

        if (audioEffects.value == null) {
            audioEffects.tryEmit(AudioEffectPlayerPresentationModel(PRESET_FLAT, FLAT))
        }

        bassBoostEffects.tryEmit(bassBoostVirtualizerPreferences.bassBoostEffects)

        if (bassBoostEffects.value == null) {
            bassBoostEffects.tryEmit(BassBoostVirtualizerEffectPlayerUIModel(0, 0))
        }

        viewModelScope.launch {

            getAllSongsFromRoomUseCase().map { songs ->

                Log.d("songs_view_model", "${songs.size}")

                songs.map { song ->

                    song.toSongIDMainPresentationModel()
                }
            }.collect { songs ->

                _allSongsState.update {
                    songs
                }
            }
        }

        viewModelScope.launch {

            getMusicSourceUseCase().map { source ->

                _userSelected.emit(true)

                when (source) {
                    is MusicSourceMusicSourceDomainModel.None -> {

                        MusicSourcePlayerPresentationModel.Default
                    }

                    is MusicSourceMusicSourceDomainModel.Source -> {

                        val mediaItems = assembleSourceMediaItemsFlowUseCase(source.songs.toList())

                        source.toMusicSourceMainPresentationModel(mediaItems)
                    }
                }

            }.collect { musicSource ->

                _musicSourceSharedFlow.emit(musicSource)
            }
        }

        viewModelScope.launch {

            getAddUpNextUseCase().map {

                val mediaItem = assembleSourceMediaItemsFlowUseCase(listOf(it))

                mediaItem.first()
            }.collect { addedUpNext ->

                _addedUpNext.emit(addedUpNext)
            }
        }

        viewModelScope.launch {

            getAddQueuedUseCase().map {

                val mediaItem = assembleSourceMediaItemsFlowUseCase(listOf(it))

                mediaItem.first()
            }.collect { addedUpNext ->

                _addedQueued.emit(addedUpNext)
            }
        }
    }

    // Will be called when exoplayer instance is created and we have audioSessionId
    fun onStart(sessionId: Int) {

        audioSessionId = sessionId

        equalizer = Equalizer(0, audioSessionId).apply {
            enabled = enableEqualizer.value
        }

        val eq = equalizer

        if (eq != null){
            val numberOfBands: Short = eq.numberOfBands
            val bandLevelRange: ShortArray = eq.bandLevelRange

            for (i in 0 until numberOfBands) {
                val freqRange: IntArray = eq.getBandFreqRange(i.toShort())
                val centerFreq: Int = eq.getCenterFreq(i.toShort())

                Log.d(
                    "EQ",
                    "Band $i: ${centerFreq / 1000} Hz (range: ${freqRange[0] / 1000}-${freqRange[1] / 1000} Hz)"
                )
            }
        }

        bassBoost = BassBoost(Int.MAX_VALUE, audioSessionId)

        bassBoost?.enabled = enableEqualizer.value

        virtualizer = Virtualizer(Int.MAX_VALUE, audioSessionId)

        virtualizer?.enabled = enableEqualizer.value

        // Set the lowest band level based on the equalizer's capabilities
        equalizerPreferences.lowestBandLevel = equalizer?.bandLevelRange?.get(0)?.toInt() ?: 0

        // Apply gain values to the equalizer based on the stored audio effects
        audioEffects.value?.gainValues?.forEachIndexed { index, value ->
            val bandLevel = (value * 1000).toInt().toShort()
            equalizer?.setBandLevel(index.toShort(), bandLevel)
        }

        bassBoostEffects.value?.bassBoostStrength?.let {

            bassBoost?.setStrength(it.toShort())
        }

        bassBoostEffects.value?.virtualizerStrength?.let {

            virtualizer?.setStrength(it.toShort())
        }
    }

    // Method called when a preset is selected
    fun onSelectPreset(presetPosition: Int) {

        // Return if no audio effects are available
        if (audioEffects.value == null) return

        // Determine the gain values based on the selected preset
        val gain = if (presetPosition == PRESET_CUSTOM) {
            ArrayList(audioEffects.value!!.gainValues)
        } else {
            ArrayList(getPresetGainValue(presetPosition))
        }

        // Update the audio effects with the selected preset and gain values
        audioEffects.tryEmit(
            AudioEffectPlayerPresentationModel(
                presetPosition,
                gain)
        )
        equalizerPreferences.audioEffects = audioEffects.value

        // Apply the gain values to the equalizer
        equalizer?.apply {
            gain.forEachIndexed { index, value ->
                val bandLevel = (value * 1000).toInt().toShort()
                setBandLevel(index.toShort(), bandLevel)
            }
        }
        val eq = equalizer

        if (eq != null){
            val numberOfBands: Short = eq.numberOfBands
            val bandLevelRange: ShortArray = eq.bandLevelRange

            for (i in 0 until numberOfBands) {
                val freqRange: IntArray = eq.getBandFreqRange(i.toShort())
                val centerFreq: Int = eq.getCenterFreq(i.toShort())

                Log.d(
                    "EQ",
                    "Band $i: ${centerFreq / 1000} Hz (range: ${freqRange[0] / 1000}-${freqRange[1] / 1000} Hz)"
                )
            }
        }
    }

    // Method called when a specific band level is changed by the user
    fun onBandLevelChanged(changedBand: Int, newGainValue: Int) {
        // Retrieve the lowest band level from preferences
        val lowest = equalizerPreferences.lowestBandLevel

        // Calculate the new band level
        val bandLevel = newGainValue.plus(lowest)

        // Apply the new band level to the equalizer
        equalizer?.setBandLevel(changedBand.toShort(), bandLevel.toShort())
        val list = ArrayList(audioEffects.value!!.gainValues)
        list[changedBand] = (newGainValue.toDouble() / 1000)
        audioEffects.tryEmit(
            AudioEffectPlayerPresentationModel(
                PRESET_CUSTOM,
                list
            )
        )
        equalizerPreferences.audioEffects = audioEffects.value
    }

    fun onBassBoostLevelChanged(newGainValue: Int) {

        // Apply the new band level to the equalizer
        bassBoost?.setStrength(newGainValue.toShort())
        bassBoostEffects.tryEmit(
            bassBoostEffects.value?.copy(
                bassBoostStrength = newGainValue,
                virtualizerStrength = bassBoostEffects.value?.virtualizerStrength?:0
            )
        )
        bassBoostVirtualizerPreferences.bassBoostEffects = bassBoostEffects.value
    }

    fun onVirtualizerLevelChanged(newGainValue: Int) {

        // Apply the new band level to the equalizer
        virtualizer?.setStrength(newGainValue.toShort())
        bassBoostEffects.tryEmit(
            bassBoostEffects.value?.copy(
                bassBoostStrength = bassBoostEffects.value?.bassBoostStrength?:0,
                virtualizerStrength = newGainValue
            )
        )
        bassBoostVirtualizerPreferences.bassBoostEffects = bassBoostEffects.value
    }

    // Method called to toggle the equalizer's enable/disable state
    fun toggleEqualizer() {
        enableEqualizer.tryEmit(!enableEqualizer.value)

        equalizer?.enabled = enableEqualizer.value

        bassBoost?.enabled = enableEqualizer.value

        virtualizer?.enabled = enableEqualizer.value

        equalizerPreferences.isEqualizerEnabled = enableEqualizer.value

        /*if (!enableEqualizer.value) {

            audioEffects.tryEmit(AudioEffectMainPresentationModel(PRESET_FLAT, FLAT))
            equalizerPreferences.audioEffects = audioEffects.value
        }*/
    }

    // Method to retrieve gain values for a specific preset
    private fun getPresetGainValue(index: Int): List<Double> {
        return when (index) {
            PRESET_FLAT -> FLAT
            PRESET_ACOUSTIC -> ACOUSTIC
            PRESET_DANCE_LOUNGE -> DANCE
            PRESET_HIP_HOP -> HIP_HOPE
            PRESET_JAZZ_BLUES -> JAZZ
            PRESET_POP -> POP
            PRESET_ROCK -> ROCK
            PRESET_PODCAST -> PODCAST
            else -> FLAT
        }
    }

    fun restoreFromPreferences() {

        viewModelScope.launch {

            val fromPreferences = playerPreferences.getPlayerStatePreference()

            val mediaItems = assembleSourceMediaItemsFlowUseCase(fromPreferences.ids.toList())

            val restored = fromPreferences.toMusicSourceMainPresentationModel(mediaItems)

            _musicSourceSharedFlow.emit(restored)

            _userSelected.emit(false)
        }
    }

    fun restoreRepeatAndShuffleFromPreferences() {

        viewModelScope.launch {

            _enableShuffle.emit(playerPreferences.getShuffleModePreference())

            _repeatMode.emit(playerPreferences.getRepeatModePreference())
        }
    }

    fun setMediaIdsPreference(ids: Set<Long>) {

        viewModelScope.launch {

            playerPreferences.updateQueue(ids = ids)
        }
    }

    /*fun enableDisableShuffle(enable: Boolean) {

        viewModelScope.launch {

            _enableShuffle.update {
                enable
            }
        }
    }

    fun setRepeatMode(repeatMode: Int) {

        viewModelScope.launch {

            _repeatMode.update {

                repeatMode
            }
        }
    }*/

    fun setCurrentIndexPreference(index: Int) {

        viewModelScope.launch {

            playerPreferences.updateCurrentIndex(index)
        }
    }

    fun addToUpNext(songId: Long) {

        viewModelScope.launch {

            _upNextState.update {

                it.plus(songId)
            }
        }
    }

    fun removeFromUpNext(songIds: List<Long>) {

        viewModelScope.launch {

            _upNextState.update {

                it.minus(songIds)
            }
        }
    }

    fun isUpNextEmpty(): Boolean {

        return _upNextState.value.isEmpty()
    }

    fun checkIfUpNextContains(songId: Long): Boolean {

        return _upNextState.value.contains(songId)
    }

    fun getUpNextIds(): List<Long> {

        return _upNextState.value
    }

    fun getLastIdOfUpNexts(): Long? {

        return _upNextState.value.lastOrNull()
    }


    /*fun getSongMetadataById(id: Long): SongMetadataMainPresentationModel? {

        viewModelScope.launch {

            getSongMetadataByIdUseCase(id).first()?.toSongMetadataMainPresentationModel()
        }
    }*/

    fun checkIfSongIsContainedInFavorites(songId: Long) {

        viewModelScope.launch {

            allSongsState.value.find { it.msId == songId }?.let { song ->

                val isContained =
                    checkIsSongContainedInPlaylistUseCase(FAVORITES_ID, song.id).first()

                _isCurrentFavorite.emit(isContained)
            }
        }
    }

    fun addRemoveFavorite(songId: Long) {

        viewModelScope.launch {

            Log.d("songs_view_model", "${allSongsState.value.size}")

            allSongsState.value.find { it.msId == songId }?.let { song ->

                Log.d("favorite_selected_view_model", "${song.id}")

                val isFavorite = checkIsSongContainedInPlaylistUseCase(FAVORITES_ID, song.id).first()

                Log.d("favorite_contained_view_model", "$isFavorite")

                if (isFavorite)
                    removeFromPlaylist(FAVORITES_ID, songId)
                else addToPlaylist(FAVORITES_ID, songId)
            }?: Log.d("favorite_selected_view_model", "not found")
        }
    }

    fun addToPlaylist(playlistId: Long, songId: Long) {

        viewModelScope.launch {

            try {
                allSongsState.value.find { it.msId == songId }?.let {
                    insertPlaylistSongUseCase(
                        playlistId = playlistId,
                        songId = it.id
                    )
                }
            } catch (e: Exception) {
                _errorSharedFlow.tryEmit(0)
            }
        }
    }

    fun removeFromPlaylist(playlistId: Long, songId: Long) {

        viewModelScope.launch {

            try {
                allSongsState.value.find { it.msId == songId }?.let {
                    deletePlaylistSongUseCase(
                        playlistId = playlistId,
                        songId = it.id
                    )
                }
            } catch (e: Exception) {
                _errorSharedFlow.tryEmit(0)
            }
        }
    }
}