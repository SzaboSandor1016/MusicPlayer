package com.example.features.player.presentation.model

import android.net.Uri

data class SongPlayerPresentationModel(
    val id: Long,
    val albumArtworkUri: Uri?,
    val name: String,
    val duration: Int,
    val author: String,
    val current: Boolean,
    val isUpNext: Boolean
) {
}