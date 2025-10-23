package com.example.features.player.presentation.model

data class PlayerStatePlayerPresentationModel(
    val currentIndex: Int,
    val displayText: String,
    val position: Long,
    val ids: Set<Long>
) {
}