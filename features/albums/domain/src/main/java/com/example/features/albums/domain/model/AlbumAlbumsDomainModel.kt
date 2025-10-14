package com.example.features.albums.domain.model

/*sealed*/data class AlbumAlbumsDomainModel(
    /*open*/ val id: Long,
    /*open*/ val name: String
) {

    /*data class Entity(
        override val id: Long,
        override val name: String,
    ): AlbumAlbumsDomainModel(id, name)*/

    /*data class Info(
        override val id: Long,
        override val name: String,
        val songs: List<SongAlbumsDomainModel>
    ): AlbumAlbumsDomainModel(id, name)*/
}