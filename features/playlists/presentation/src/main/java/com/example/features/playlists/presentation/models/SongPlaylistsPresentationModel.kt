package com.example.features.playlists.presentation.models

data class SongPlaylistsPresentationModel(
    val id: Long,
    val playlistId: Long,
    val displayName: String,
    val duration: Int,
    val author: String
) {
}