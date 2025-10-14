package com.example.features.albums.presentation.model

data class AlbumAlbumsPresentationModel(
    val id: Long,
    val name: String,
    val songs: List<SongAlbumsPresentationModel>
) {
}