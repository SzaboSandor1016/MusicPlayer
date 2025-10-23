package com.example.features.playlists.presentation.models

data class SongPlaylistsPresentationModel(
    val id: Long,
    val msId: Long,
    val albumId: Long,
    val playlistId: Long,
    val displayName: String,
    val duration: Int,
    val artist: String
) {
}