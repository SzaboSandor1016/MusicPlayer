package com.example.features.playlists.domain.models

data class PlaylistPlaylistsDomainModel(
    val id: Long,
    val label: String,
    val type: Int,
    val songs: List<SongPlaylistsDomainModel>
) {
}