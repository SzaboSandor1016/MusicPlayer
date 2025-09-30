package com.example.features.musicsource.domain.repository

import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import kotlinx.coroutines.flow.Flow

interface MusicSourceRepository {

    fun getMusicSource(): Flow<MusicSourceMusicSourceDomainModel>

    suspend fun setMusicSource(source: MusicSourceMusicSourceDomainModel)

    fun getAddUpNext(): Flow<Long>

    suspend fun addUpNext(songId: Long)

    fun getAddQueued(): Flow<Long>
    suspend fun addQueued(songId: Long)
}