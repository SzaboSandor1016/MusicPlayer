package com.example.features.playlists.domain.models

data class SongPlaylistsDomainModel(
    val id: Long,
    val playlistId: Long,
    val displayName: String,
    val duration: Int,
    val author: String
) {
}