package com.example.musicplayer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.features.albums.domain.usecase.GetAllAlbumsUseCase
import com.example.musicplayer.mappers.toAlbumMainPresentationModel
import com.example.musicplayer.models.AlbumMainPresentationModel
import com.example.sync.domain.usecases.SyncRoomWithMediaStoreUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ViewModelMain(
    private val getAllAlbumsUseCase: GetAllAlbumsUseCase,
    private val syncRoomWithMediaStoreUseCase: SyncRoomWithMediaStoreUseCase,
): ViewModel() {

    val allAlbumsState: StateFlow<List<AlbumMainPresentationModel>> by lazy {

        getAllAlbumsUseCase().map {albums ->

            albums.map { it.toAlbumMainPresentationModel() }
        }.flowOn(
            Dispatchers.IO
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    private val _selectedTabIndexState: MutableStateFlow<Int> = MutableStateFlow(0)

    val selectedTabIndexState: StateFlow<Int> = _selectedTabIndexState.asStateFlow()

    fun selectTab(index: Int) {

        viewModelScope.launch {

            _selectedTabIndexState.update {

                index
            }
        }
    }

    fun syncRoomWithMediaStore() {

        viewModelScope.launch {

            syncRoomWithMediaStoreUseCase()
        }
    }
}