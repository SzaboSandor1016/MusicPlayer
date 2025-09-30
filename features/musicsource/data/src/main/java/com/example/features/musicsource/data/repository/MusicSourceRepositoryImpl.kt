package com.example.features.musicsource.data.repository

import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.musicsource.domain.repository.MusicSourceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class MusicSourceRepositoryImpl: MusicSourceRepository {

    private val musicSourceDispatcher = Dispatchers.IO
    private val musicSourceState: MutableStateFlow<MusicSourceMusicSourceDomainModel> =
        MutableStateFlow(MusicSourceMusicSourceDomainModel.None)

    private val addUpNext: MutableSharedFlow<Long> = MutableSharedFlow()

    private val addQueued: MutableSharedFlow<Long> = MutableSharedFlow()

    override fun getMusicSource(): Flow<MusicSourceMusicSourceDomainModel> {

        return musicSourceState.map { it }
    }

    override suspend fun setMusicSource(source: MusicSourceMusicSourceDomainModel) {

        withContext(musicSourceDispatcher) {

            musicSourceState.update {

                source
            }
        }
    }

    override fun getAddUpNext(): Flow<Long> {

        return addUpNext.map { it }
    }

    override suspend fun addUpNext(songId: Long) {

        withContext(musicSourceDispatcher){
            addUpNext.emit(songId)
        }
    }

    override fun getAddQueued(): Flow<Long> {

        return addQueued.map { it }
    }

    override suspend fun addQueued(songId: Long) {
        withContext(musicSourceDispatcher){
            addQueued.emit(songId)
        }
    }
}