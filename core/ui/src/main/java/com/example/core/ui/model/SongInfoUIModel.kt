package com.example.core.ui.model

data class SongInfoUIModel(
    val id: Long,
    val albumId: Long,
    val name: String,
    val duration: Int,
    val artist: String
) {
}