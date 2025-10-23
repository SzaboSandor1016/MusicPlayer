package com.example.features.songs.domain.model

sealed class SongSongsDomainModel(
    open val id: Long,
    open val msId: Long,
    open val name: String,
    open val duration: Int,
    open val albumId: Long,
    open val artistId: Long,
    open val genreId: Long,
) {

    data class Entity(
        override val id: Long,
        override val msId: Long,
        override val name: String,
        override val duration: Int,
        override val albumId: Long,
        override val artistId: Long,
        override val genreId: Long,
        val dateAdded: Long,
        val key: String
    ): SongSongsDomainModel(id ,msId, name, duration, albumId, artistId, genreId)

    data class Info(
        override val id: Long,
        override val msId: Long,
        override val name: String,
        override val duration: Int,
        override val albumId: Long,
        override val artistId: Long,
        override val genreId: Long,
        val artist: String,
        val dateAdded: Long,
    ): SongSongsDomainModel(id ,msId, name, duration, albumId, artistId, genreId)
}