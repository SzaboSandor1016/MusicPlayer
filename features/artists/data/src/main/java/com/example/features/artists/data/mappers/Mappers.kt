package com.example.features.artists.data.mappers

import com.example.datasources.database.dao.ArtistDao.*
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.SongEntity
import com.example.datasources.mediastore.domain.models.ArtistMediaStoreLocalDatasourceModel
import com.example.features.artists.domain.model.ArtistArtistsDomainModel

fun ArtistArtistsDomainModel/*.Entity*/.toArtistEntity(): ArtistEntity {

    return ArtistEntity(
        id = this.id,
        name = this.name
    )
}

/*fun ArtistWithSongs.toArtistArtistsDomainModelInfo(): ArtistArtistsDomainModel.Info {

    return ArtistArtistsDomainModel.Info(
        id = this.artist.id,
        name = this.artist.name,
        albums = this.songs.map { it.toAlbumArtistsDomainModel(this.artist.name) }
    )
}

fun ArtistWithSongs.toAlbumArtistsDomainModel(artist: String): AlbumArtistsDomainModel {

    return AlbumArtistsDomainModel(
        id = this.album.id,
        name = this.album.name,
        songs = this.songs.map{ it.toAlbumSongDomainModel(artist)}
    )
}*/

/*fun SongEntity.toAlbumSongDomainModel(artist: String): SongArtistsDomainModel {

    return SongArtistsDomainModel(
        id = this.id,
        displayName = this.displayName,
        artist = artist,
        duration = this.duration
    )
}*/

fun ArtistEntity.toArtistArtistsDomainModelEntity(): ArtistArtistsDomainModel/*.Entity*/ {

    return ArtistArtistsDomainModel/*.Entity*/(
        id = this.id,
        name = this.name
    )
}

fun ArtistMediaStoreLocalDatasourceModel.toArtistArtistsDomainModelEntity(): ArtistArtistsDomainModel/*.Entity*/ {

    return ArtistArtistsDomainModel/*.Entity*/(
        id = this.id,
        name = this.name
    )
}