package com.example.musicplayer.models

data class PlayerStateMainPresentationModel(
    val currentIndex: Int,
    val displayText: String,
    val position: Long,
    val ids: Set<Long>
) {
}