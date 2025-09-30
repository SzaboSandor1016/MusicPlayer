package com.example.musicplayer.models

data class SongMainPresentationModel(
    val id: Long,
    val name: String,
    val duration: Int,
    val author: String,
    val current: Boolean,
    val isUpNext: Boolean
) {
}