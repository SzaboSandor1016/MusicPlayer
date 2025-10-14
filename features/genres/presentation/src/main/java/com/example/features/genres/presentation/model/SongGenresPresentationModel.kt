package com.example.features.genres.presentation.model

data class SongGenresPresentationModel(
    val id: Long,
    val msId: Long,
    val displayName: String,
    val artist: String,
    val duration: Int
) {
}