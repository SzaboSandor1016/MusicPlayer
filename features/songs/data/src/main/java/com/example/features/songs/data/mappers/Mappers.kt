package com.example.features.songs.data.mappers

import com.example.datasources.database.dao.entities.SongEntity
import com.example.datasources.mediastore.domain.models.SongMetadataSongsLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.SongSongsLocalDatasourceModel
import com.example.features.songs.domain.model.SongMetadataSongsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel

fun SongEntity.toSongSongsDomainModel(): SongSongsDomainModel {

    return SongSongsDomainModel(
        id = this.id,
        name = this.displayName,
        duration = this.duration,
        author = this.author,
        albumId = this.album,
        dateAdded = this.dateAdded
    )
}

fun SongSongsDomainModel.toSongEntity(): SongEntity {

    return SongEntity(
        id = this.id,
        displayName = this.name,
        duration = this.duration,
        author = this.author,
        album = this.albumId,
        dateAdded = this.dateAdded
    )
}

fun SongSongsLocalDatasourceModel.toSongSongsDomainModel(): SongSongsDomainModel {

    return SongSongsDomainModel(
        id = this.id,
        name = this.displayName,
        duration = this.duration,
        author = this.author,
        albumId = this.albumId,
        dateAdded = this.dateAdded
    )
}

fun SongMetadataSongsLocalDatasourceModel.toSongMetadataSongsDomainModel(): SongMetadataSongsDomainModel {

    return SongMetadataSongsDomainModel(
        displayName = this.displayName,
        duration = this.duration,
        author = this.author,
        albumId = this.albumId
    )
}