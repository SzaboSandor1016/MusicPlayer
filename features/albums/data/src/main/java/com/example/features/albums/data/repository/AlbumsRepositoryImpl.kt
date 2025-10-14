package com.example.features.albums.data.repository

import com.example.datasources.mediastore.domain.MediaStoreLocalDatasource
import com.example.features.albums.data.mappers.toAlbumAlbumsDomainModelEntity
import com.example.features.albums.domain.datasource.AlbumsDatasource
import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import com.example.features.albums.domain.repository.AlbumsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AlbumsRepositoryImpl(
    private val albumsDatasource: AlbumsDatasource,
    private val mediaStoreLocalDatasource: MediaStoreLocalDatasource
): AlbumsRepository {
    override suspend fun insertAlbums(albums: List<AlbumAlbumsDomainModel/*.Entity*/>) {

        albumsDatasource.insertAlbums(albums)
    }

    override suspend fun deleteAlbums(albums: List<AlbumAlbumsDomainModel/*.Entity*/>) {

        albumsDatasource.deleteAlbums(albums)
    }

    override fun getAllAlbumsFromRoom(): Flow<List<AlbumAlbumsDomainModel/*.Entity*/>> {

        return albumsDatasource.getAllAlbumsFromRoom()
    }

    override fun getAllAlbumsFromMediaStore(): Flow<List<AlbumAlbumsDomainModel/*.Entity*/>> {
        return mediaStoreLocalDatasource.getListOfAlbums().map { albums ->

            albums.map { it.toAlbumAlbumsDomainModelEntity() }
        }
    }

    /*override fun getAllAlbumsWithArtistAndSongs(): Flow<List<AlbumAlbumsDomainModel.Info>> {

        return albumsDatasource.getAllAlbumsWithArtistAndSongsFromRoom()
    }*/
}