package com.example.datasources.mediastore.domain

import android.graphics.Bitmap
import android.net.Uri
import com.example.datasources.mediastore.domain.models.SongMetadataSongsLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.SongSongsLocalDatasourceModel
import kotlinx.coroutines.flow.Flow

interface SongsLocalDatasource {

    fun getAllSongsOnDevice(): Flow<List<SongSongsLocalDatasourceModel>>

    fun getMediaItemFromId(id: Long): SongMetadataSongsLocalDatasourceModel?

    /*fun getMediaItemAlbumFromId(uri: Uri): Bitmap?

    fun getEmbeddedAlbumArt(uri: Uri): Bitmap?*/
}