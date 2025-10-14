package com.example.features.albums.domain.repository

import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import kotlinx.coroutines.flow.Flow

interface AlbumsRepository {

    suspend fun insertAlbums(albums: List<AlbumAlbumsDomainModel/*.Entity*/>)

    suspend fun deleteAlbums(albums: List<AlbumAlbumsDomainModel/*.Entity*/>)

    fun getAllAlbumsFromRoom(): Flow<List<AlbumAlbumsDomainModel/*.Entity*/>>

    fun getAllAlbumsFromMediaStore(): Flow<List<AlbumAlbumsDomainModel/*.Entity*/>>

    //fun getAllAlbumsWithArtistAndSongs(): Flow<List<AlbumAlbumsDomainModel.Info>>
}