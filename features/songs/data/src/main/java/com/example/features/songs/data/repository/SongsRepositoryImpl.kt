package com.example.features.songs.data.repository

import com.example.datasources.mediastore.domain.MediaStoreLocalDatasource
import com.example.features.songs.data.mappers.toSongMetadataSongsDomainModel
import com.example.features.songs.data.mappers.toSongSongsDomainModelInfo
import com.example.features.songs.domain.datasource.SongsRoomDatasource
import com.example.features.songs.domain.model.SongMetadataSongsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel
import com.example.features.songs.domain.repository.SongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SongsRepositoryImpl(
    private val mediaStoreLocalDatasource: MediaStoreLocalDatasource,
    private val songsRoomDatasource: SongsRoomDatasource
): SongsRepository {

    private val songsRepositoryCoroutineDispatcher = Dispatchers.IO

    override suspend fun insertSongs(songs: List<SongSongsDomainModel.Entity>) {

        withContext(songsRepositoryCoroutineDispatcher) {

            songsRoomDatasource.insertSongs(songs)
        }
    }

    override suspend fun updateSongs(songs: List<SongSongsDomainModel.Entity>) {

        withContext(songsRepositoryCoroutineDispatcher) {

            songsRoomDatasource.updateSongs(songs)
        }
    }

    override suspend fun deleteSongs(songs: List<SongSongsDomainModel.Entity>) {

        withContext(songsRepositoryCoroutineDispatcher) {

            songsRoomDatasource.deleteSongs(songs)
        }
    }

    override fun getSongMetadataFromIdFlow(id: Long): Flow<SongMetadataSongsDomainModel?> {

        return flowOf(mediaStoreLocalDatasource.getMediaItemFromId(id)).map {

            it?.toSongMetadataSongsDomainModel()
        }
    }

    override fun getSongMetadataFromIdSync(id: Long): SongMetadataSongsDomainModel? {

        return mediaStoreLocalDatasource.getMediaItemFromId(id)?.toSongMetadataSongsDomainModel()
    }

    /*override fun getSongThumbnailByUriFlow(uri: Uri): Flow<Bitmap?> {
        return flowOf(songsLocalDatasource.getMediaItemAlbumFromId(uri))
    }

    override fun getSongThumbnailByUriSync(uri: Uri): Bitmap? {

        return songsLocalDatasource.getMediaItemAlbumFromId(uri)
    }

    override fun getEmbeddedAlbumArtFromIdFlow(uri: Uri): Flow<Bitmap?> {

        return flowOf(songsLocalDatasource.getEmbeddedAlbumArt(uri))
    }

    override fun getEmbeddedAlbumArtFromIdSync(uri: Uri): Bitmap? {

        return songsLocalDatasource.getEmbeddedAlbumArt(uri)
    }*/

    override fun getAllSongsInfoFromRoom(): Flow<List<SongSongsDomainModel.Info>> {

        return songsRoomDatasource.getAllSongsWithArtists()
    }

    override fun getAllSongsEntityFromRoom(): Flow<List<SongSongsDomainModel.Entity>> {
        return songsRoomDatasource.getAllSongs()
    }

    override fun getAllSongsFromMediaStore(): Flow<List<SongSongsDomainModel.Entity>> {

        return mediaStoreLocalDatasource.getAllSongsOnDevice().map { songs ->
            songs.map { it.toSongSongsDomainModelInfo() }
        }
    }
}