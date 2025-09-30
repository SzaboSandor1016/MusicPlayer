package com.example.features.songs.domain.datasource

import com.example.features.songs.domain.model.SongSongsDomainModel
import kotlinx.coroutines.flow.Flow

interface SongsRoomDatasource {

    suspend fun insertSongs(songs: List<SongSongsDomainModel>)

    suspend fun deleteSongs(songs: List<SongSongsDomainModel>)

    suspend fun updateSongs(songs: List<SongSongsDomainModel>)

    fun getAllSongs(): Flow<List<SongSongsDomainModel>>
}