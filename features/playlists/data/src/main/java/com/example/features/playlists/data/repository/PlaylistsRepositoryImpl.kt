package com.example.features.playlists.data.repository

import com.example.features.playlists.domain.datasource.PlaylistsRoomDatasource
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class PlaylistsRepositoryImpl(
    private val playlistsRoomDatasource: PlaylistsRoomDatasource
): PlaylistsRepository {

    override suspend fun insertNewPlaylist(playlist: PlaylistPlaylistsDomainModel/*.Entity*/) {

        playlistsRoomDatasource.insertNewPlaylist(
            playlist = playlist
        )
    }

    override suspend fun insertPlaylistSongs(playlistSongs: List<PlaylistSongPlaylistsDomainModel>) {

        playlistsRoomDatasource.insertPlaylistSongs(
            playlistSongs = playlistSongs
        )
    }

    override fun getPlaylistSongsByPlaylistId(playlistId: Long): Flow<List<PlaylistSongPlaylistsDomainModel>> {

        return playlistsRoomDatasource.getPlaylistSongsByPlaylistId(
            playlistId = playlistId
        )
    }

    override fun getPlaylistById(playlistId: Long): Flow<PlaylistPlaylistsDomainModel/*.Info*/> {

        return playlistsRoomDatasource.getPlaylistById(
            playlistId = playlistId
        )
    }

    override fun getAllAssociations(): Flow<List<PlaylistSongPlaylistsDomainModel>> {
        return playlistsRoomDatasource.getAllAssociations()
    }

    override suspend fun deletePlaylist(playlistId: Long) {

        playlistsRoomDatasource.deletePlaylist(
            playlistId = playlistId
        )
    }

    override suspend fun insertPlaylistSong(playlistSong: PlaylistSongPlaylistsDomainModel) {

        playlistsRoomDatasource.insertPlaylistSong(
            playlistSong = playlistSong
        )
    }

    override suspend fun updatePlaylistSongs(playlistSongs: List<PlaylistSongPlaylistsDomainModel>) {

        playlistsRoomDatasource.updatePlaylistSongs(
            playlistSongs = playlistSongs
        )
    }

    override suspend fun deletePlaylistSongs(playlistSongs: List<PlaylistSongPlaylistsDomainModel>) {

        playlistsRoomDatasource.deletePlaylistSongs(
            playlistSongs = playlistSongs
        )
    }

    override suspend fun deletePlaylistSong(playlistId: Long, songId: Long) {

        playlistsRoomDatasource.deletePlaylistSong(
            playlistId = playlistId,
            songId = songId
        )
    }

    override fun isSongContainedInPlaylist(
        playlistId: Long,
        songId: Long
    ): Flow<Boolean> {

        return playlistsRoomDatasource.isSongContainedInPlaylist(playlistId, songId)
    }

    override fun getAllPlaylistsFromRoom(): Flow<List<PlaylistPlaylistsDomainModel/*.Info*/>> {

        return playlistsRoomDatasource.getAllPlaylistsFromRoom()
    }
}