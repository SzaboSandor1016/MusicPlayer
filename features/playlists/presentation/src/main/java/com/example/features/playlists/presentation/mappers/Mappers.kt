package com.example.features.playlists.presentation.mappers

import com.example.core.ui.grid.model.GridItem
import com.example.core.ui.model.SongInfoUIModel
import com.example.features.musicsource.domain.models.SongMusicSourceDomainModel
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.presentation.models.PlaylistPlaylistsPresentationModel
import com.example.features.playlists.presentation.models.SongPlaylistsPresentationModel
import com.example.features.songs.domain.model.SongSongsDomainModel

fun PlaylistPlaylistsDomainModel.toPlaylistPlaylistsPresentationModel(songs: List<SongPlaylistsPresentationModel>): PlaylistPlaylistsPresentationModel {

    return PlaylistPlaylistsPresentationModel(
        id = this.id,
        albumId = songs.firstOrNull()?.albumId?: -1L,
        label = this.label,
        songs = songs
    )
}

fun SongSongsDomainModel.Info.toSongPlaylistsPresentationModel(playlistId: Long): SongPlaylistsPresentationModel {

    return SongPlaylistsPresentationModel(
        id = this.id,
        msId = this.msId,
        albumId = this.albumId,
        playlistId = playlistId,
        displayName = this.name,
        duration = this.duration,
        artist = this.artist
    )
}

fun SongPlaylistsPresentationModel.toSongMusicSourceDomainModel(): SongMusicSourceDomainModel {

    return SongMusicSourceDomainModel(
        id = this.id
    )
}

fun SongPlaylistsPresentationModel.toSongInfoUIModel(): SongInfoUIModel {
    
    return SongInfoUIModel(
        id = this.msId,
        albumId = this.albumId,
        name = this.displayName,
        duration = this.duration,
        artist = this.artist
    )
}

fun PlaylistPlaylistsPresentationModel.toGridItem(
    label: String,
    action: (Long) -> Unit,
    action1: (Long) -> Unit
): GridItem.Item {

    return GridItem.Item(
        action = action,
        actionAll = action1,
        itemId = this.id,
        albumId = this.albumId,
        label = label
    )
}