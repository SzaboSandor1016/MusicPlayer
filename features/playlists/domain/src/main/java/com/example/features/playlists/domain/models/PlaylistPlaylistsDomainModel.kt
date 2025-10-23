package com.example.features.playlists.domain.models

/*sealed*/data class PlaylistPlaylistsDomainModel(
    val id: Long,
    /*open*/ val label: String,
    /*open*/ val type: Int,
) {

    /*data class Entity(
        override val label: String,
        override val type: Int
    ): PlaylistPlaylistsDomainModel(label, type)

    data class Info(
        val id: Long,
        override val label: String,
        override val type: Int,
        val songs: List<SongPlaylistsDomainModel>
    ): PlaylistPlaylistsDomainModel(label,type)*/
}