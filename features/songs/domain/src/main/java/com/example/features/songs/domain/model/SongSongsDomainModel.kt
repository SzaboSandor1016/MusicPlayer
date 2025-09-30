package com.example.features.songs.domain.model

data class SongSongsDomainModel(
    val id: Long,
    val name: String,
    val duration: Int,
    val author: String,
    val albumId: Long,
    val dateAdded: Long
) {
}