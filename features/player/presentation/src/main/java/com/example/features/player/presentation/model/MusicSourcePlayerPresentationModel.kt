package com.example.features.player.presentation.model

import androidx.media3.common.MediaItem

sealed interface MusicSourcePlayerPresentationModel{

    data object Default: MusicSourcePlayerPresentationModel

    data class MusicSource(
        val fromPrefs: Boolean,
        val displayText: String,
        val selectedIndex: Int,
        val position: Long,
        val songs: List<MediaItem>
    ): MusicSourcePlayerPresentationModel
}