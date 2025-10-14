package com.example.features.albums.domain.model

data class SongAlbumsDomainModel(
    val id: Long,
    val msId: Long,
    val displayName: String,
    val artist: String,
    val duration: Int
) {
}