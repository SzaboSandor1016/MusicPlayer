package com.example.features.songs.domain.model

data class SongMetadataSongsDomainModel(
    val displayName: String,
    val duration: Int,
    val author: String,
    val albumId: Long
) {
}