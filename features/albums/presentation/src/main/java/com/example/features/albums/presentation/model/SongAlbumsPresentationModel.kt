package com.example.features.albums.presentation.model

data class SongAlbumsPresentationModel(
    val id: Long,
    val msId: Long,
    val displayName: String,
    val artist: String,
    val duration: Int
) {
}