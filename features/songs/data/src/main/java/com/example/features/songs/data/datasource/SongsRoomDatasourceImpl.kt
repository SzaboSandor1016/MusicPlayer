package com.example.features.songs.data.datasource

import com.example.datasources.database.dao.SongDao
import com.example.features.songs.data.mappers.toSongEntity
import com.example.features.songs.data.mappers.toSongSongsDomainModel
import com.example.features.songs.domain.datasource.SongsRoomDatasource
import com.example.features.songs.domain.model.SongSongsDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongsRoomDatasourceImpl(
    private val songDao: SongDao
): SongsRoomDatasource {

    override suspend fun insertSongs(songs: List<SongSongsDomainModel>) {

        songDao.insertSongs(
            songs = songs.map { it.toSongEntity() }
        )
    }

    override suspend fun deleteSongs(songs: List<SongSongsDomainModel>) {

        songDao.deleteSongs(
            songs = songs.map {
                it.toSongEntity()
            }
        )
    }

    override suspend fun updateSongs(songs: List<SongSongsDomainModel>) {

        songDao.updateSongs(
            songs = songs.map {
                it.toSongEntity()
            }
        )
    }

    override fun getAllSongs(): Flow<List<SongSongsDomainModel>> {

        return songDao.getAllSongs().map { songs ->
            songs.map { it.toSongSongsDomainModel() }
        }
    }
}