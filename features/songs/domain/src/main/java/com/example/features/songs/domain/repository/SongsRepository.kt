package com.example.features.songs.domain.repository

import com.example.features.songs.domain.model.SongMetadataSongsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel
import kotlinx.coroutines.flow.Flow

interface SongsRepository {

    suspend fun insertSongs(songs: List<SongSongsDomainModel>)

    suspend fun updateSongs(songs: List<SongSongsDomainModel>)

    suspend fun deleteSongs(songs: List<SongSongsDomainModel>)

    fun getSongMetadataFromIdFlow(id: Long): Flow<SongMetadataSongsDomainModel?>

    fun getSongMetadataFromIdSync(id: Long): SongMetadataSongsDomainModel?

    /*fun getSongThumbnailByUriFlow(uri: Uri): Flow<Bitmap?>

    fun getSongThumbnailByUriSync(uri: Uri): Bitmap?

    fun getEmbeddedAlbumArtFromIdFlow(uri: Uri): Flow<Bitmap?>

    fun getEmbeddedAlbumArtFromIdSync(uri: Uri): Bitmap?*/

    fun getAllSongsFromRoom(): Flow<List<SongSongsDomainModel>>

    fun getAllSongsFromMediaStore(): Flow<List<SongSongsDomainModel>>
}