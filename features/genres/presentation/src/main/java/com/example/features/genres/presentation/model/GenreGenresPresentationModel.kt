package com.example.features.genres.presentation.model

data class GenreGenresPresentationModel(
    val id: Long,
    val albumId: Long,
    val name: String,
    val songs: List<SongGenresPresentationModel>
) {
}