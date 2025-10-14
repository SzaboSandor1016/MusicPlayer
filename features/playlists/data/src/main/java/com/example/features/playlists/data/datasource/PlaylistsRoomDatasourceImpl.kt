package com.example.features.playlists.data.datasource

import com.example.datasources.database.dao.PlaylistsDao
import com.example.features.playlists.data.mappers.toPlaylistEntity
import com.example.features.playlists.data.mappers.toPlaylistPlaylistsDomainModel
import com.example.features.playlists.data.mappers.toPlaylistSongDomainModel
import com.example.features.playlists.data.mappers.toPlaylistSongEntity
import com.example.features.playlists.domain.datasource.PlaylistsRoomDatasource
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRoomDatasourceImpl(
    private val playlistsDao: PlaylistsDao
): PlaylistsRoomDatasource {
    override suspend fun insertNewPlaylist(playlist: PlaylistPlaylistsDomainModel/*.Entity*/) {

        playlistsDao.insertNewPlaylist(
            playlistEntity = playlist.toPlaylistEntity()
        )
    }

    override suspend fun deletePlaylist(playlistId: Long) {

        playlistsDao.deletePlaylist(
            playlistId = playlistId
        )
    }

    override suspend fun insertPlaylistSong(playlistSong: PlaylistSongPlaylistsDomainModel) {

        playlistsDao.insertNewPlaylistSong(
            playlistSongEntity = playlistSong.toPlaylistSongEntity()
        )
    }

    override suspend fun insertPlaylistSongs(playlistSongs: List<PlaylistSongPlaylistsDomainModel>) {

        playlistsDao.insertNewPlaylistSongs(
            playlistSongEntities = playlistSongs.map { it.toPlaylistSongEntity() }
        )
    }

    override suspend fun updatePlaylistSongs(playlistSongs: List<PlaylistSongPlaylistsDomainModel>) {

        playlistsDao.updatePlaylistSongs(
            playlistSongs = playlistSongs.map { it.toPlaylistSongEntity() }
        )
    }

    override suspend fun deletePlaylistSong(playlistId: Long, songId: Long) {

        playlistsDao.deletePlaylistSong(
            playlistId = playlistId,
            songId = songId
        )
    }

    override fun isSongContainedInPlaylist(
        playlistId: Long,
        songId: Long
    ): Flow<Boolean> {

        return playlistsDao.getAssociationByPlaylistAndSongId(playlistId, songId).map {

            it != null
        }
    }

    override fun getPlaylistSongsByPlaylistId(playlistId: Long): Flow<List<PlaylistSongPlaylistsDomainModel>> {

        return playlistsDao.getAssociationsByPlaylistId(
            playlistId = playlistId
        ).map { playlistSongs ->

            playlistSongs.map { it.toPlaylistSongDomainModel() }
        }
    }

    override fun getPlaylistById(playlistId: Long): Flow<PlaylistPlaylistsDomainModel/*.Info*/> {

        return playlistsDao.getPlaylistById(playlistId).map {
            it.toPlaylistPlaylistsDomainModel()
        }
    }

    override fun getAllAssociations(): Flow<List<PlaylistSongPlaylistsDomainModel>> {
        return playlistsDao.getAllAssociations().map { playlistSongs ->
            playlistSongs.map { it.toPlaylistSongDomainModel() }
        }
    }

    override fun getAllPlaylistsFromRoom(): Flow<List<PlaylistPlaylistsDomainModel/*.Info*/>> {

        return playlistsDao.getAllPlaylists().map { playlists ->

            playlists.map { it.toPlaylistPlaylistsDomainModel() }
        }
    }
}