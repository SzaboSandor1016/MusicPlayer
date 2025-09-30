package com.example.features.playlists.presentation.models

data class PlaylistPlaylistsPresentationModel(
    val id: Long,
    val label: String,
    val songs: List<SongPlaylistsPresentationModel>
) {
}