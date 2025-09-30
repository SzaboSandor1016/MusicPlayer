package com.example.musicplayer.models

import androidx.media3.common.MediaItem

sealed interface MusicSourceMainPresentationModel{

    data object Default: MusicSourceMainPresentationModel

    data class MusicSource(
        val fromPrefs: Boolean,
        val displayText: String,
        val selectedIndex: Int,
        val position: Long,
        val songs: List<MediaItem>
    ): MusicSourceMainPresentationModel
}