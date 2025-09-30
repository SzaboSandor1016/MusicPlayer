package com.example.features.playlists.data.mappers

import com.example.datasources.database.dao.PlaylistsDao.SongWithPlaylistSong
import com.example.datasources.database.dao.PlaylistsDao.PlaylistWithSongs
import com.example.datasources.database.dao.entities.PlaylistEntity
import com.example.datasources.database.dao.entities.PlaylistSongEntity
import com.example.datasources.database.dao.entities.SongEntity
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import com.example.features.playlists.domain.models.SongPlaylistsDomainModel

fun PlaylistPlaylistsDomainModel.toPlaylistEntity(): PlaylistEntity {
    
    return PlaylistEntity(
        label = this.label,
        type = this.type
    )
}

fun PlaylistPlaylistsDomainModel.toFullPlaylistEntity(): PlaylistEntity {

    return PlaylistEntity(
        id = this.id,
        label = this.label,
        type = this.type
    )
}

fun PlaylistSongPlaylistsDomainModel.toPlaylistSongEntity(): PlaylistSongEntity {

    return PlaylistSongEntity(
        playlistId = this.playlistId,
        songId = this.songId,
        order = this.order
    )
}

fun PlaylistSongEntity.toPlaylistSongDomainModel(): PlaylistSongPlaylistsDomainModel {

    return PlaylistSongPlaylistsDomainModel(
        playlistId = this.playlistId,
        songId = this.songId,
        order = this.order
    )
}

fun PlaylistWithSongs.toPlaylistPlaylistsDomainModel(): PlaylistPlaylistsDomainModel {

    return PlaylistPlaylistsDomainModel(
        id = this.playlistEntity.id,
        label = this.playlistEntity.label,
        type = this.playlistEntity.type,
        songs = this.songs.sortedBy { it.playlistSongEntity.order }.map { it.toSongPlaylistsDomainModel() }
    )
}
fun SongWithPlaylistSong.toSongPlaylistsDomainModel(): SongPlaylistsDomainModel {

    return SongPlaylistsDomainModel(
        id = this.songEntity.id,
        playlistId = this.playlistSongEntity.playlistId,
        displayName = this.songEntity.displayName,
        duration = this.songEntity.duration,
        author = this.songEntity.author
    )
}
