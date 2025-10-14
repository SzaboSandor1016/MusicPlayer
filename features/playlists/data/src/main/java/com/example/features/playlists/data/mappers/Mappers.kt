package com.example.features.playlists.data.mappers

import android.util.Log
import com.example.datasources.database.dao.entities.PlaylistEntity
import com.example.datasources.database.dao.entities.PlaylistSongEntity
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel

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

fun PlaylistEntity.toPlaylistPlaylistsDomainModel(): PlaylistPlaylistsDomainModel {

    return PlaylistPlaylistsDomainModel(
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

/*fun PlaylistWithSongs.toPlaylistPlaylistsDomainModel(): PlaylistPlaylistsDomainModel.Info {

    return PlaylistPlaylistsDomainModel.Info(
        id = this.playlistEntity.id,
        label = this.playlistEntity.label,
        type = this.playlistEntity.type,
        songs = this.songs.sortedBy { it.playlistSongEntity.order }.map { it.toSongPlaylistsDomainModel() }
    )
}
fun SongWithPlaylistSong.toSongPlaylistsDomainModel(): SongPlaylistsDomainModel {

    if(this.songEntity.artist == null) {
        Log.d("null_artist", this.songEntity.song.displayName)
    }

    return SongPlaylistsDomainModel(
        id = this.songEntity.song.id,
        playlistId = this.playlistSongEntity.playlistId,
        displayName = this.songEntity.song.displayName,
        duration = this.songEntity.song.duration,
        author = this.songEntity.artist.name
    )
}*/
