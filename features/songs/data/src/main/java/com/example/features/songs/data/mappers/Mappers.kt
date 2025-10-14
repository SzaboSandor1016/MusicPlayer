package com.example.features.songs.data.mappers

import com.example.datasources.database.dao.SongDao.SongWithArtist
import com.example.datasources.database.dao.entities.SongEntity
import com.example.datasources.mediastore.domain.models.SongMetadataMediaStoreLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.SongMediaStoreLocalDatasourceModel
import com.example.features.songs.domain.model.SongMetadataSongsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel

fun SongEntity.toSongSongsDomainModelEntity(): SongSongsDomainModel.Entity {

    return SongSongsDomainModel.Entity(
        id = this.id,
        msId = this.msId,
        name = this.displayName,
        duration = this.duration,
        albumId = this.albumId,
        artistId = this.artistId,
        genreId = this.genreId,
        dateAdded = this.dateAdded,
        key = this.key
    )
}

fun SongWithArtist.toSongSongsDomainModelInfo(): SongSongsDomainModel.Info {

    return SongSongsDomainModel.Info(
        id = this.song.id,
        msId = this.song.msId,
        name = this.song.displayName,
        duration = this.song.duration,
        albumId = this.song.albumId,
        artistId = this.song.artistId,
        genreId = this.song.genreId,
        artist = this.artist,
        dateAdded = this.song.dateAdded
    )
}

fun SongSongsDomainModel.Entity.toSongEntity(): SongEntity {

    return SongEntity(
        msId = this.msId,
        displayName = this.name,
        duration = this.duration,
        artistId = this.artistId,
        albumId = this.albumId,
        genreId = this.genreId,
        dateAdded = this.dateAdded,
        key = this.key
    )
}

fun SongSongsDomainModel.Entity.toSongEntityWithId(): SongEntity {

    return SongEntity(
        id = this.id,
        msId = this.msId,
        displayName = this.name,
        duration = this.duration,
        artistId = this.artistId,
        albumId = this.albumId,
        genreId = this.genreId,
        dateAdded = this.dateAdded,
        key = this.key
    )
}

fun SongMediaStoreLocalDatasourceModel.toSongSongsDomainModelInfo(): SongSongsDomainModel.Entity {

    return SongSongsDomainModel.Entity(
        id = 0,
        msId = this.id,
        name = this.displayName,
        duration = this.duration,
        albumId = this.albumId,
        artistId = this.artistId,
        genreId = this.genreId,
        dateAdded = this.dateAdded,
        key = this.key
    )
}

fun SongMetadataMediaStoreLocalDatasourceModel.toSongMetadataSongsDomainModel(): SongMetadataSongsDomainModel {

    return SongMetadataSongsDomainModel(
        displayName = this.displayName,
        duration = this.duration,
        author = this.author,
        albumId = this.albumId
    )
}