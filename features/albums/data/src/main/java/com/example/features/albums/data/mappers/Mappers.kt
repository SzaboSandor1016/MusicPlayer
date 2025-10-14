package com.example.features.albums.data.mappers

import com.example.datasources.database.dao.AlbumDao.*
import com.example.datasources.database.dao.entities.AlbumEntity
import com.example.datasources.database.dao.entities.SongEntity
import com.example.datasources.mediastore.domain.models.AlbumMediaStoreLocalDatasourceModel
import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import com.example.features.albums.domain.model.SongAlbumsDomainModel

fun AlbumAlbumsDomainModel.toAlbumEntity(): AlbumEntity {
    
    return AlbumEntity(
        id = this.id,
        name = this.name,
    )
}

/*fun AlbumWithSongs.toAlbumAlbumsDomainModelInfo(): AlbumAlbumsDomainModel.Info {

    return AlbumAlbumsDomainModel.Info(
        id = this.album.id,
        name = this.album.name,
        songs = this.songs.map { it.toSongAlbumsDomainModel() }
    )
}

fun SongWithArtist.toSongAlbumsDomainModel(): SongAlbumsDomainModel {

    return SongAlbumsDomainModel(
        id = this.song.id,
        msId = this.song.msId,
        displayName = this.song.displayName,
        artist = this.artist.name,
        duration = this.song.duration
    )
}*/

fun AlbumEntity.toAlbumAlbumsDomainModelEntity(): AlbumAlbumsDomainModel/*.Entity*/ {

    return AlbumAlbumsDomainModel/*.Entity*/(
        id = this.id,
        name = this.name,
    )
}

fun AlbumMediaStoreLocalDatasourceModel.toAlbumAlbumsDomainModelEntity(): AlbumAlbumsDomainModel/*.Entity*/ {

    return AlbumAlbumsDomainModel/*.Entity*/(
        id = this.id,
        name = this.name,
    )
}
