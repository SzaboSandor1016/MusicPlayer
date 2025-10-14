package com.example.features.albums.domain.datasource

import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import kotlinx.coroutines.flow.Flow

interface AlbumsDatasource {

    suspend fun insertAlbums(albums: List<AlbumAlbumsDomainModel/*.Entity*/>)

    suspend fun deleteAlbums(albums: List<AlbumAlbumsDomainModel/*.Entity*/>)

    fun getAllAlbumsFromRoom(): Flow<List<AlbumAlbumsDomainModel/*.Entity*/>>

    //fun getAllAlbumsWithArtistAndSongsFromRoom(): Flow<List<AlbumAlbumsDomainModel.Info>>
}