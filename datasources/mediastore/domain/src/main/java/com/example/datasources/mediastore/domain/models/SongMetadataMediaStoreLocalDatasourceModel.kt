package com.example.datasources.mediastore.domain.models

data class SongMetadataMediaStoreLocalDatasourceModel(
    val displayName: String,
    val duration: Int,
    val author: String,
    val albumId: Long
) {
}