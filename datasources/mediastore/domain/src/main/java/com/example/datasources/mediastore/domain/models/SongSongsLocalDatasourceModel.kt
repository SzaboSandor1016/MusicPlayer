package com.example.datasources.mediastore.domain.models

data class SongSongsLocalDatasourceModel(
    val id: Long,
    val displayName: String,
    val duration: Int,
    val author: String,
    val albumId: Long,
    val dateAdded: Long
) {
}