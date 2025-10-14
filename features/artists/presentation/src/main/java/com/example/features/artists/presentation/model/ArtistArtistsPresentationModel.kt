package com.example.features.artists.presentation.model

data class ArtistArtistsPresentationModel(
    val id: Long,
    val name: String,
    val albums: List<AlbumArtistsPresentationModel>,
    val songs: List<SongArtistsPresentationModel>
) {
}