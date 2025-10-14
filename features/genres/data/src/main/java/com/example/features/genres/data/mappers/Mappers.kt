package com.example.features.genres.data.mappers

import com.example.datasources.database.dao.GenreDao.*
import com.example.datasources.database.dao.entities.GenreEntity
import com.example.datasources.mediastore.domain.models.GenreMediaStoreLocalDatasourceModel
import com.example.features.genres.domain.model.GenreGenresDomainModel

fun GenreEntity.toGenreGenresDomainModelEntity(): GenreGenresDomainModel/*.Entity*/ {

    return GenreGenresDomainModel/*.Entity*/(
        id = this.id,
        name = this.name
    )
}

fun GenreGenresDomainModel/*.Entity*/.toGenreEntity(): GenreEntity {

    return GenreEntity(
        id = this.id,
        name = this.name
    )
}

fun GenreMediaStoreLocalDatasourceModel.toGenreGenresDomainModelEntity(): GenreGenresDomainModel/*.Entity*/ {

    return GenreGenresDomainModel/*.Entity*/(
        id = this.id,
        name = this.name
    )
}

/*
fun GenreWithSongs.toGenreGenresDomainModelInfo(): GenreGenresDomainModel.Info {

    return GenreGenresDomainModel.Info(
        id = this.genre.id,
        name = this.genre.name,
        songs = this.songs.map { it.toSongGenresDomainModel() }
    )
}

fun SongWithArtist.toSongGenresDomainModel(): SongGenresDomainModel {

    return SongGenresDomainModel(
        id = this.song.id,
        displayName = this.song.displayName,
        artist = this.artist.name,
        duration = this.song.duration
    )
}*/
