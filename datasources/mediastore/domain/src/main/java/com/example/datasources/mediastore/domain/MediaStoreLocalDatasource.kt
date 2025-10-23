package com.example.datasources.mediastore.domain

import com.example.datasources.mediastore.domain.models.AlbumMediaStoreLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.ArtistMediaStoreLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.GenreMediaStoreLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.SongMetadataMediaStoreLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.SongMediaStoreLocalDatasourceModel
import kotlinx.coroutines.flow.Flow

interface MediaStoreLocalDatasource {

    fun getAllSongsOnDevice(): Flow<List<SongMediaStoreLocalDatasourceModel>>

    fun getMediaItemFromId(id: Long): SongMetadataMediaStoreLocalDatasourceModel?

    fun getListOfGenres(): Flow<List<GenreMediaStoreLocalDatasourceModel>>

    fun getListOfAlbums(): Flow<List<AlbumMediaStoreLocalDatasourceModel>>

    fun getListOfArtists(): Flow<List<ArtistMediaStoreLocalDatasourceModel>>

    /*fun getMediaItemAlbumFromId(uri: Uri): Bitmap?

    fun getEmbeddedAlbumArt(uri: Uri): Bitmap?*/
}