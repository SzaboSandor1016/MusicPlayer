package com.example.features.musicsource.domain.models

sealed interface MusicSourceMusicSourceDomainModel {

    data class Source(
        val initialIndex: Int,
        val displayText: String,
        val songs: List<Long>
    ): MusicSourceMusicSourceDomainModel

    /*data class Playlist(
        val initialIndex: Int,
        val displayText: String,
        val songs: List<Long>
    ): MusicSourceMusicSourceDomainModel*/

    data object None: MusicSourceMusicSourceDomainModel
}