package com.example.features.albums.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.values.AUTO_PLAYLIST_IDS
import com.example.features.albums.domain.usecase.GetAllAlbumsUseCase
import com.example.features.albums.presentation.mappers.toAlbumAlbumsPresentationModel
import com.example.features.albums.presentation.mappers.toMusicSource
import com.example.features.albums.presentation.mappers.toPlaylistInfoPresentationModel
import com.example.features.albums.presentation.model.AlbumAlbumsPresentationModel
import com.example.features.albums.presentation.model.PlaylistInfoAlbumsPresentationModel
import com.example.features.musicsource.domain.usecases.AddQueuedUseCase
import com.example.features.musicsource.domain.usecases.AddUpNextUseCase
import com.example.features.musicsource.domain.usecases.SetMusicSourceUseCase
import com.example.features.playlists.domain.usecases.GetAllPlaylistsFromRoomUseCase
import com.example.features.playlists.domain.usecases.InsertPlaylistSongUseCase
import com.example.features.songs.domain.model.SongSongsDomainModel
import com.example.features.songs.domain.usecase.GetAllSongsFromRoomUseCase
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

class ViewModelAlbums(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val getAllSongsFromRoomUseCase: GetAllSongsFromRoomUseCase,
    private val setMusicSourceUseCase: SetMusicSourceUseCase,
    private val getAllPlaylistsFromRoomUseCase: GetAllPlaylistsFromRoomUseCase,
    private val insertPlaylistSongUseCase: InsertPlaylistSongUseCase,
    private val addUpNextUseCase: AddUpNextUseCase,
    private val addQueuedUseCase: AddQueuedUseCase
): ViewModel() {

    val playlistsInfoState: StateFlow<List<PlaylistInfoAlbumsPresentationModel>> by lazy {

        getAllPlaylistsFromRoomUseCase().map { playlists ->

            playlists.filter{ it.id !in AUTO_PLAYLIST_IDS }.map { it.toPlaylistInfoPresentationModel() }
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    val songsState: StateFlow<List<SongSongsDomainModel.Info>> by lazy {

        getAllSongsFromRoomUseCase().map { songs ->

            songs
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    val allAlbumsState: StateFlow<List<AlbumAlbumsPresentationModel>> by lazy {

        combine(
            getAllAlbumsUseCase(),
            songsState
        ) { albums, songs ->

            albums.map { album ->

                val albumSongs = songs.filter { it.albumId == album.id }.sortedBy { it.dateAdded }

                album.toAlbumAlbumsPresentationModel(
                    albumSongs
                )
            }
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    private val _selectedAlbumIdState = MutableStateFlow<Long?>(null)

    private val _selectedAlbumState: MutableStateFlow<AlbumAlbumsPresentationModel?> =
        MutableStateFlow(null)

    val selectedAlbumState = _selectedAlbumState.asStateFlow()

    private val _errorSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()

    val errorSharedFlow: SharedFlow<Int> = _errorSharedFlow.asSharedFlow()

    init {

        viewModelScope.launch {

            combine(
                allAlbumsState,
                _selectedAlbumIdState
            ) { allAlbums, selectedId ->

                allAlbums.firstOrNull { it.id == selectedId }
            }.collect { album ->

                _selectedAlbumState.update {

                    album
                }
            }
        }
    }

    fun setSelectedId(selectedId: Long?) {

        viewModelScope.launch {

            _selectedAlbumIdState.update {

                selectedId
            }
        }
    }

    fun getSelectedId(): Long? {

        return _selectedAlbumState.value?.id
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

    fun addToPlaylist(playlistId: Long, songId: Long) {

        viewModelScope.launch {

            try {
                songsState.value.find { it.msId == songId }?.let {
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
    
    fun setMusicSource(
        selectedAlbumId: Long,
        selectedId: Long? = null
    ) {
        
        viewModelScope.launch {

            allAlbumsState.value.find {
                it.id == selectedAlbumId
            }?.let { selectedAlbum ->

                val index = selectedAlbum.songs.indexOfFirst { it.msId == selectedId }.let {
                    if(it == -1) {
                        0
                    } else {
                        it
                    }
                }

                setMusicSourceUseCase(
                    source = selectedAlbum.toMusicSource(index)
                )
            }
        }
    }
}