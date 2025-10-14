package com.example.features.albums.data.datasource

import com.example.datasources.database.dao.AlbumDao
import com.example.features.albums.data.mappers.toAlbumAlbumsDomainModelEntity
import com.example.features.albums.data.mappers.toAlbumEntity
import com.example.features.albums.domain.datasource.AlbumsDatasource
import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlbumsDatasourceImpl(
    private val albumDao: AlbumDao
): AlbumsDatasource {

    override suspend fun insertAlbums(albums: List<AlbumAlbumsDomainModel/*.Entity*/>) {

        albumDao.insertAlbums(
            albums = albums.map { it.toAlbumEntity() }
        )
    }

    override suspend fun deleteAlbums(albums: List<AlbumAlbumsDomainModel/*.Entity*/>) {

        albumDao.deleteAlbums(
            albums = albums.map { it.toAlbumEntity() }
        )
    }

    override fun getAllAlbumsFromRoom(): Flow<List<AlbumAlbumsDomainModel/*.Entity*/>> {

        return albumDao.getAllAlbums().map { albums ->
            albums.map { it.toAlbumAlbumsDomainModelEntity() }
        }
    }

    /*override fun getAllAlbumsWithArtistAndSongsFromRoom(): Flow<List<AlbumAlbumsDomainModel.Info>> {

        return albumDao.getAllAlbumsWithSongs().map { albums ->

            albums.map { it.toAlbumAlbumsDomainModelInfo() }
        }
    }*/
}