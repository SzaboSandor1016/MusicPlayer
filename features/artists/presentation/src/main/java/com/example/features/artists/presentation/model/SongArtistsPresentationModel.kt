package com.example.features.artists.presentation.model

data class SongArtistsPresentationModel(
    val id: Long,
    val msId: Long,
    val albumId: Long,
    val displayName: String,
    val artist: String,
    val duration: Int
) {
}