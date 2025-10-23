package com.example.features.artists.presentation.model

data class AlbumArtistsPresentationModel(
    val id: Long,
    val name: String,
    val songs: List<SongArtistsPresentationModel>
){
}