package com.example.features.genres.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.common.values.AUTO_PLAYLIST_IDS
import com.example.features.genres.domain.usecase.GetAllGenresUseCase
import com.example.features.genres.presentation.mappers.toGenreGenresPresentationModel
import com.example.features.genres.presentation.mappers.toMusicSource
import com.example.features.genres.presentation.mappers.toPlaylistInfoPresentationModel
import com.example.features.genres.presentation.mappers.toSongGenresPresentationModel
import com.example.features.genres.presentation.model.GenreGenresPresentationModel
import com.example.features.genres.presentation.model.PlaylistInfoGenresPresentationModel
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

class ViewModelGenres(
    private val getAllGenresUseCase: GetAllGenresUseCase,
    private val getAllSongsFromRoomUseCase: GetAllSongsFromRoomUseCase,
    private val setMusicSourceUseCase: SetMusicSourceUseCase,
    private val getAllPlaylistsFromRoomUseCase: GetAllPlaylistsFromRoomUseCase,
    private val insertPlaylistSongUseCase: InsertPlaylistSongUseCase,
    private val addUpNextUseCase: AddUpNextUseCase,
    private val addQueuedUseCase: AddQueuedUseCase
): ViewModel() {

    val playlistsInfoState: StateFlow<List<PlaylistInfoGenresPresentationModel>> by lazy {

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

    val allGenresState: StateFlow<List<GenreGenresPresentationModel>> by lazy {

        combine(
        getAllGenresUseCase(),
            songsState
        ) { genresFlow, songsFlow ->

            genresFlow.map { genre ->

                val songs = songsFlow
                    .filter { it.genreId == genre.id }
                    .map { it.toSongGenresPresentationModel() }

                genre.toGenreGenresPresentationModel(songs)
            }
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    private val _selectedGenreIdState = MutableStateFlow<Long?>(null)

    private val _selectedGenreState: MutableStateFlow<GenreGenresPresentationModel?> =
        MutableStateFlow(null)

    val selectedGenreState = _selectedGenreState.asStateFlow()

    private val _errorSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()

    val errorSharedFlow: SharedFlow<Int> = _errorSharedFlow.asSharedFlow()

    init {

        viewModelScope.launch {

            combine(
                allGenresState,
                _selectedGenreIdState
            ) { allGenres, selectedId ->

                allGenres.firstOrNull { it.id == selectedId }
            }.collect { genre ->

                _selectedGenreState.update {

                    genre
                }
            }
        }
    }

    fun setSelectedGenreId(selectedId: Long?) {

        viewModelScope.launch {

            _selectedGenreIdState.update {

                selectedId
            }
        }
    }

    fun getSelectedGenreId(): Long? {

        return _selectedGenreState.value?.id
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
        genreId: Long,
        songId: Long? = null
    ) {

        viewModelScope.launch {

            allGenresState.value.firstOrNull { it.id == genreId }?.let { genre ->

                val initialIndex = genre.songs.indexOfFirst { it.msId == songId }.let {

                    if(it == -1) {
                        0
                    } else {
                        it
                    }
                }

                setMusicSourceUseCase(
                    source = genre.toMusicSource(initialIndex)
                )
            }
        }
    }
}