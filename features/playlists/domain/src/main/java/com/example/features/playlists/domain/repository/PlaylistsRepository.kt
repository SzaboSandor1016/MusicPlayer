package com.example.features.playlists.domain.repository

import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun insertNewPlaylist(playlist: PlaylistPlaylistsDomainModel/*.Entity*/)

    suspend fun deletePlaylist(playlistId: Long)

    suspend fun insertPlaylistSong(playlistSong: PlaylistSongPlaylistsDomainModel)

    suspend fun insertPlaylistSongs(playlistSongs: List<PlaylistSongPlaylistsDomainModel>)

    suspend fun updatePlaylistSongs(playlistSongs: List<PlaylistSongPlaylistsDomainModel>)

    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

    fun isSongContainedInPlaylist(playlistId: Long, songId: Long): Flow<Boolean>

    fun getPlaylistSongsByPlaylistId(playlistId: Long): Flow<List<PlaylistSongPlaylistsDomainModel>>

    fun getPlaylistById(playlistId: Long): Flow<PlaylistPlaylistsDomainModel/*.Info*/>

    fun getAllAssociations(): Flow<List<PlaylistSongPlaylistsDomainModel>>

    fun getAllPlaylistsFromRoom(): Flow<List<PlaylistPlaylistsDomainModel/*.Info*/>>
}