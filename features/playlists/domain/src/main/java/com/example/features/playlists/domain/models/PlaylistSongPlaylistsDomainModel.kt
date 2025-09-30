package com.example.features.playlists.domain.models

data class PlaylistSongPlaylistsDomainModel(
    val playlistId: Long,
    val songId: Long,
    val order: Int
) {
}