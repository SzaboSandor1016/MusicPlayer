package com.example.features.genres.domain.model

/*sealed*/data class GenreGenresDomainModel(
    /*open*/ val id: Long,
    /*open*/ val name: String
) {

    /*data class Entity(
        override val id: Long,
        override val name: String
    ): GenreGenresDomainModel(id, name)

    data class Info(
        override val id: Long,
        override val name: String,
        val songs: List<SongGenresDomainModel>
    ): GenreGenresDomainModel(id, name)*/
}