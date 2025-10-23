package com.example.datasources.mediastore.domain.models

data class SongMediaStoreLocalDatasourceModel(
    val id: Long,
    val displayName: String,
    val duration: Int,
    val artistId: Long,
    val albumId: Long,
    val genreId: Long,
    val dateAdded: Long,
    val key: String
) {
}