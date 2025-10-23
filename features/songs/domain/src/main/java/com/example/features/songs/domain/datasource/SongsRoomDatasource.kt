package com.example.features.songs.domain.datasource

import com.example.features.songs.domain.model.SongSongsDomainModel
import kotlinx.coroutines.flow.Flow

interface SongsRoomDatasource {

    suspend fun insertSongs(songs: List<SongSongsDomainModel.Entity>)

    suspend fun deleteSongs(songs: List<SongSongsDomainModel.Entity>)

    suspend fun updateSongs(songs: List<SongSongsDomainModel.Entity>)

    fun getAllSongs(): Flow<List<SongSongsDomainModel.Entity>>

    fun getAllSongsWithArtists(): Flow<List<SongSongsDomainModel.Info>>
}