package com.example.features.songs.presentation.vievmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.values.AUTO_PLAYLIST_IDS
import com.example.core.common.values.FAVORITES_ID
import com.example.core.common.values.RECENT_ID
import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.musicsource.domain.usecases.AddQueuedUseCase
import com.example.features.musicsource.domain.usecases.AddUpNextUseCase
import com.example.features.musicsource.domain.usecases.SetMusicSourceUseCase
import com.example.features.playlists.domain.usecases.GetAllPlaylistsFromRoomUseCase
import com.example.features.playlists.domain.usecases.InsertPlaylistSongUseCase
import com.example.features.songs.domain.usecase.GetAllSongsFromRoomUseCase
import com.example.features.songs.presentation.mappers.toPlaylistInfoPresentationModel
import com.example.features.songs.presentation.mappers.toSongSongsPresentationModel
import com.example.features.songs.presentation.models.PlaylistInfoSongsPresentationModel
import com.example.features.songs.presentation.models.SongSongsPresentationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelSongs(
    private val getAllPlaylistsFromRoomUseCase: GetAllPlaylistsFromRoomUseCase,
    private val getAllSongsFromRoomUseCase: GetAllSongsFromRoomUseCase,
    private val setMusicSourceUseCase: SetMusicSourceUseCase,
    private val insertPlaylistSongUseCase: InsertPlaylistSongUseCase,
    private val addUpNextUseCase: AddUpNextUseCase,
    private val addQueuedUseCase: AddQueuedUseCase
): ViewModel() {

    val playlistsInfoState: StateFlow<List<PlaylistInfoSongsPresentationModel>> by lazy {

        getAllPlaylistsFromRoomUseCase().map { playlists ->

            playlists.filter { it.id !in AUTO_PLAYLIST_IDS }.map { it.toPlaylistInfoPresentationModel() }
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    val allSongsState: StateFlow<List<SongSongsPresentationModel>> by lazy {

        getAllSongsFromRoomUseCase().map { songs ->

            songs.sortedBy { it.dateAdded }.map { it.toSongSongsPresentationModel() }
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    private val _searchExpressionState: MutableStateFlow<String> = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val songsState: StateFlow<List<SongSongsPresentationModel>> = _searchExpressionState.flatMapLatest {expression ->

        allSongsState.map { songs ->

            songs.filter { it.name.lowercase().contains(expression) || it.author.lowercase().contains(expression) }
        }
    }.flowOn(
        Dispatchers.IO
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _errorSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()

    val errorSharedFlow: SharedFlow<Int> = _errorSharedFlow.asSharedFlow()
    
    fun setMusicSource(selectedItemId: Long) {
        
        viewModelScope.launch {

            val item = allSongsState.value.find { it.msId == selectedItemId }

            if (item != null) {

                val index = allSongsState.value.indexOf(item)

                //TODO create mapper
                val musicSource = MusicSourceMusicSourceDomainModel.Source(
                    initialIndex = index,
                    displayText = "All",
                    songs = allSongsState.value.map { it.msId/*.toSongMusicSourceDomainModel()*/ }
                )

                setMusicSourceUseCase(
                    source = musicSource
                )
            }
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

    fun addToUpNext(songId: Long) {

        viewModelScope.launch {

            addUpNextUseCase(songId)
        }
    }

    fun addToQueue(songId: Long) {

        viewModelScope.launch {

            addQueuedUseCase(songId)
        }
    }

    fun setSearchExpression(expression: String) {

        viewModelScope.launch {

            _searchExpressionState.update {

                expression
            }
        }
    }
}