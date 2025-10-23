package com.example.features.songs.data.datasource

import com.example.datasources.database.dao.SongDao
import com.example.features.songs.data.mappers.toSongEntity
import com.example.features.songs.data.mappers.toSongEntityWithId
import com.example.features.songs.data.mappers.toSongSongsDomainModelInfo
import com.example.features.songs.data.mappers.toSongSongsDomainModelEntity
import com.example.features.songs.domain.datasource.SongsRoomDatasource
import com.example.features.songs.domain.model.SongSongsDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongsRoomDatasourceImpl(
    private val songDao: SongDao
): SongsRoomDatasource {

    override suspend fun insertSongs(songs: List<SongSongsDomainModel.Entity>) {

        songDao.insertSongs(
            songs = songs.map { it.toSongEntity() }
        )
    }

    override suspend fun deleteSongs(songs: List<SongSongsDomainModel.Entity>) {

        songDao.deleteSongs(
            songs = songs.map {
                it.toSongEntityWithId()
            }
        )
    }

    override suspend fun updateSongs(songs: List<SongSongsDomainModel.Entity>) {

        songDao.updateSongs(
            songs = songs.map {
                it.toSongEntityWithId()
            }
        )
    }

    override fun getAllSongs(): Flow<List<SongSongsDomainModel.Entity>> {

        return songDao.getAllSongs().map { songs ->

            songs.map {it.toSongSongsDomainModelEntity()}
        }
    }


    override fun getAllSongsWithArtists(): Flow<List<SongSongsDomainModel.Info>> {

        return songDao.getAllSongsWithArtists().map { songs ->
            songs.map { it.toSongSongsDomainModelInfo() }
        }
    }
}