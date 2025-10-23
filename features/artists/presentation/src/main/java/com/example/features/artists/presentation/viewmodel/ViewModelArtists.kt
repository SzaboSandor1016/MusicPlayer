package com.example.features.artists.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.values.AUTO_PLAYLIST_IDS
import com.example.features.albums.domain.usecase.GetAllAlbumsUseCase
import com.example.features.artists.domain.usecase.GetAllArtistsUseCase
import com.example.features.artists.presentation.mappers.toAlbumArtistsPresentationModel
import com.example.features.artists.presentation.mappers.toArtistArtistsPresentationModel
import com.example.features.artists.presentation.mappers.toMusicSource
import com.example.features.artists.presentation.mappers.toPlaylistInfoPresentationModel
import com.example.features.artists.presentation.mappers.toSongArtistsPresentationModel
import com.example.features.artists.presentation.model.AlbumArtistsPresentationModel
import com.example.features.artists.presentation.model.ArtistArtistsPresentationModel
import com.example.features.artists.presentation.model.PlaylistInfoArtistsPresentationModel
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

class ViewModelArtists(
    private val getAllArtistsUseCase: GetAllArtistsUseCase,
    private val getAllSongsFromRoomUseCase: GetAllSongsFromRoomUseCase,
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val setMusicSourceUseCase: SetMusicSourceUseCase,
    private val getAllPlaylistsFromRoomUseCase: GetAllPlaylistsFromRoomUseCase,
    private val insertPlaylistSongUseCase: InsertPlaylistSongUseCase,
    private val addUpNextUseCase: AddUpNextUseCase,
    private val addQueuedUseCase: AddQueuedUseCase
): ViewModel() {

    val playlistsInfoState: StateFlow<List<PlaylistInfoArtistsPresentationModel>> by lazy {

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

            songs.sortedBy { it.name.lowercase() }
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    val allArtistsState: StateFlow<List<ArtistArtistsPresentationModel>> by lazy {

        combine(
            getAllArtistsUseCase(),
            getAllAlbumsUseCase(),
            songsState
        ) { artists, albums, songs ->

            val albumsWithSongs = songs.groupBy { it.albumId }

            val artistsWithSongs = songs.groupBy { it.artistId }

            val albumsMapped = albums.map { album ->

                val songs = albumsWithSongs[album.id].let {

                    it ?: emptyList()
                }
                album.toAlbumArtistsPresentationModel(songs)
            }

            artists.map { artist ->

                val songs = artistsWithSongs[artist.id].let {
                    it ?: emptyList()
                }.map { it.toSongArtistsPresentationModel() }

                val songIds = songs.map { it.id }.toSet()

                val albums = albumsMapped.filter { album ->

                    album.songs.any { it.id in songIds }
                }

                artist.toArtistArtistsPresentationModel(
                    albums = albums,
                    songs = songs
                )
            }

            /*val albumsWithSongs = albums.map { album ->

                val albumSongs = songs.filter { it.albumId == album.id }*//*.sortedBy { it.dateAdded }*//*

                album to albumSongs
            }

            artists.map { artist ->

                val artistSongs = songs.filter { it.artistId == artist.id }

                *//*val songAlbums = artistSongs.map { it.albumId }.distinct()

                val albs = albums.filter { it.id in songAlbums }.map { album ->

                    val albumSongs = songs.filter { it.albumId == album.id }

                    album.toAlbumArtistsPresentationModel(albumSongs)
                }*//*

                val artistAlbums = albumsWithSongs.filter { album ->

                    artistSongs.any { it in album.second }
                }

                artist.toArtistArtistsPresentationModel(
                    albums = artistAlbums.map { album ->
                        album.first.toAlbumArtistsPresentationModel(album.second)
                    },
                    songs = artistSongs*//*.sortedBy { it.dateAdded }*//*.map { it.toSongArtistsPresentationModel() }
                )
            }*/
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    private val _selectedArtistIdState = MutableStateFlow<Long?>(null)

    private val _selectedArtistState: MutableStateFlow<ArtistArtistsPresentationModel?> =
        MutableStateFlow(null)

    val selectedArtistState = _selectedArtistState.asStateFlow()

    private val _selectedAlbumIdState = MutableStateFlow<Long?>(null)

    private val _selectedAlbumState: MutableStateFlow<AlbumArtistsPresentationModel?> =
        MutableStateFlow(null)

    val selectedAlbumState = _selectedAlbumState.asStateFlow()

    private val _errorSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()

    val errorSharedFlow: SharedFlow<Int> = _errorSharedFlow.asSharedFlow()

    init {

        viewModelScope.launch {

            combine(
                allArtistsState,
                _selectedArtistIdState
            ) { allArtists, selectedId ->

                allArtists.let { allArtists ->

                    allArtists.firstOrNull { it.id == selectedId }
                }
            }.collect { artist ->

                _selectedArtistState.update {

                    artist
                }
            }
        }

        viewModelScope.launch {

            combine(
                _selectedArtistState,
                _selectedAlbumIdState
            ) { selectedArtist, selectedId ->

                selectedArtist?.let { artist ->
                    artist.albums.firstOrNull { it.id == selectedId }
                }
            }.collect { album ->

                _selectedAlbumState.update {

                    album
                }
            }
        }
    }

    fun setSelectedArtistId(selectedId: Long?) {

        viewModelScope.launch {

            _selectedArtistIdState.update {
                selectedId
            }
        }
    }

    fun setSelectedAlbumId(selectedId: Long?) {

        viewModelScope.launch {

            _selectedAlbumIdState.update {

                selectedId
            }
        }
    }

    fun getSelectedAlbumId(): Long? {

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

    fun setArtistSongMusicSource(
        songId: Long? = null
    ) {

        viewModelScope.launch {

            _selectedArtistState.value?.let { artist ->

                val initialIndex = artist.songs.indexOfFirst { it.msId == songId }.let {
                    if (it == -1) {
                        0
                    } else {
                        it
                    }
                }

                setMusicSourceUseCase(
                    source = artist.toMusicSource(initialIndex)
                )
            }
        }
    }

    fun setArtistAlbumMusicSource(
        albumId: Long,
        albumSongId: Long? = null
    ) {

        viewModelScope.launch {

            _selectedArtistState.value?.let { artist ->

                artist.albums.firstOrNull { it.id == albumId }?.let { album ->

                    val index = album.songs.indexOfFirst { it.msId == albumSongId }.let {

                        if(it == -1) {
                            0
                        } else {
                            it
                        }
                    }

                    setMusicSourceUseCase(
                        source = album.toMusicSource(index)
                    )
                }
            }
        }
    }
}