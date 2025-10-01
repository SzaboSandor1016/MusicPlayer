package com.example.features.playlists.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.values.FAVORITES_ID
import com.example.core.common.values.PLAYLIST_USER
import com.example.core.common.values.RECENT_ID
import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.musicsource.domain.usecases.AddQueuedUseCase
import com.example.features.musicsource.domain.usecases.AddUpNextUseCase
import com.example.features.musicsource.domain.usecases.SetMusicSourceUseCase
import com.example.features.playlists.domain.usecases.DeletePlaylistSongUseCase
import com.example.features.playlists.domain.usecases.DeletePlaylistUseCase
import com.example.features.playlists.domain.usecases.GetAllPlaylistsFromRoomUseCase
import com.example.features.playlists.domain.usecases.InsertNewPlaylistUseCase
import com.example.features.playlists.domain.usecases.InsertPlaylistSongUseCase
import com.example.features.playlists.presentation.mappers.toPlaylistPlaylistsPresentationModel
import com.example.features.playlists.presentation.mappers.toSongMusicSourceDomainModel
import com.example.features.playlists.presentation.models.PlaylistPlaylistsPresentationModel
import com.example.features.playlists.presentation.models.SelectedPlaylistPlaylistsPresentationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelPlaylists(
    private val getAllPlaylistsFromRoomUseCase: GetAllPlaylistsFromRoomUseCase,
    private val insertNewPlaylistUseCase: InsertNewPlaylistUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val setMusicSourceUseCase: SetMusicSourceUseCase,
    private val insertPlaylistSongUseCase: InsertPlaylistSongUseCase,
    private val deletePlaylistSongUseCase: DeletePlaylistSongUseCase,
    private val addUpNextUseCase: AddUpNextUseCase,
    private val addQueuedUseCase: AddQueuedUseCase
): ViewModel() {

    private val autoPlaylistIds = listOf(FAVORITES_ID,RECENT_ID)

    private val _allPlaylistsState: MutableStateFlow<List<PlaylistPlaylistsPresentationModel>> = MutableStateFlow(emptyList())

    val allPlaylistsState: StateFlow<List<PlaylistPlaylistsPresentationModel>> = _allPlaylistsState.asStateFlow()

    /*private val _autoPlaylistsState: MutableStateFlow<List<PlaylistPlaylistsPresentationModel>> = MutableStateFlow(emptyList())

    val autoPlaylistsState: StateFlow<List<PlaylistPlaylistsPresentationModel>> = _autoPlaylistsState.asStateFlow()*/

    private val _selectedPlaylistIdState: MutableStateFlow<Long?> = MutableStateFlow(null)

    private val _selectedPlaylistState: MutableStateFlow<SelectedPlaylistPlaylistsPresentationModel> = MutableStateFlow(
        SelectedPlaylistPlaylistsPresentationModel.Default
    )

    val selectedPlaylistState: StateFlow<SelectedPlaylistPlaylistsPresentationModel> = _selectedPlaylistState.asStateFlow()

    private val _errorSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()

    val errorSharedFlow: SharedFlow<Int> = _errorSharedFlow.asSharedFlow()

    init {


        viewModelScope.launch {
            getAllPlaylistsFromRoomUseCase().map { playlists ->

                playlists.map {
                    it.toPlaylistPlaylistsPresentationModel()
                }
            }.flowOn(
                Dispatchers.IO
            ).collect { playlists ->

                _allPlaylistsState.update {

                    playlists
                }
            }
        }

        viewModelScope.launch {

            combine(
                _allPlaylistsState,
                _selectedPlaylistIdState
            ) { allPlaylists, selectedId ->

                if (selectedId != null) {

                    val playlist = allPlaylists.find { it.id == selectedId }

                    if (playlist != null) {

                        Log.d("playlist", "found")

                        SelectedPlaylistPlaylistsPresentationModel.Selected(playlist)
                    } else {

                        Log.d("playlist", "not found")

                        SelectedPlaylistPlaylistsPresentationModel.Default
                    }
                } else {
                    SelectedPlaylistPlaylistsPresentationModel.Default
                }

            }
            .flowOn(Dispatchers.IO)
            .collect { selectedPlaylist ->

                _selectedPlaylistState.update {
                    selectedPlaylist
                }
            }
        }
    }

    fun insertNewPlaylist(playlistName: String) {

        viewModelScope.launch {

            insertNewPlaylistUseCase(
                playlistName = playlistName,
                playlistType = PLAYLIST_USER
            )
        }
    }

    fun deletePlaylist(playlistId: Long) {

        viewModelScope.launch {

            deletePlaylistUseCase(
                playlistId = playlistId
            )
        }
    }

    fun selectPlaylist(playlistId: Long) {

        viewModelScope.launch {

            _selectedPlaylistIdState.update {

                playlistId
            }

            /*_allPlaylistsState.value.find { it.id == playlistId}?.let { playlist ->

                _selectedPlaylistState.update {

                    SelectedPlaylistPlaylistsPresentationModel.Selected(playlist)
                }
            }*/
        }
    }

    fun unselectPlaylist() {

        viewModelScope.launch {

            _selectedPlaylistIdState.update {

                null
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

    fun setMusicSource(
        playlistId: Long,
        selectedIndex: Int = 0
    ) {

        viewModelScope.launch {

            _allPlaylistsState.value.find { it.id == playlistId }?.let { selectedPlaylist ->

                val musicSource = MusicSourceMusicSourceDomainModel.Source(
                    initialIndex = selectedIndex,
                    displayText = selectedPlaylist.label,
                    songs = selectedPlaylist.songs.map { it.id/*toSongMusicSourceDomainModel()*/ }
                )

                setMusicSourceUseCase(
                    musicSource
                )
            }
        }
    }

    fun addToPlaylist(playlistId: Long, songId: Long) {

        viewModelScope.launch {

            try {
                insertPlaylistSongUseCase(
                    playlistId = playlistId,
                    songId = songId
                )
            } catch (e: Exception) {

                _errorSharedFlow.tryEmit(0)
            }
        }
    }
    
    fun removeFromPlaylist(playlistId: Long, songId: Long) {
        
        viewModelScope.launch { 
            
            deletePlaylistSongUseCase(
                playlistId = playlistId,
                songId = songId
            )
        }
    }
}