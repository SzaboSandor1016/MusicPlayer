package com.example.features.artists.domain.model

/*sealed*/data class ArtistArtistsDomainModel(
    /*open*/ val id: Long,
    /*open*/ val name: String
) {

    /*data class Entity(
        override val id: Long,
        override val name: String
    ): ArtistArtistsDomainModel(id,name)

    data class Info(
        override val id: Long,
        override val name: String,
        val albums: List<AlbumArtistsDomainModel>
    ): ArtistArtistsDomainModel(id,name)*/
}