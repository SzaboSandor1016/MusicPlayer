package com.example.datasources.mediastore.domain.models

data class SongMetadataSongsLocalDatasourceModel(
    val displayName: String,
    val duration: Int,
    val author: String,
    val albumId: Long
) {
}