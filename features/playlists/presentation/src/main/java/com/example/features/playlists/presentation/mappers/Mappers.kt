package com.example.features.playlists.presentation.mappers

import com.example.core.common.values.FAVORITES_NAME
import com.example.core.ui.grid.model.GridItem
import com.example.core.ui.model.SongInfoUIModel
import com.example.features.musicsource.domain.models.SongMusicSourceDomainModel
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.models.SongPlaylistsDomainModel
import com.example.features.playlists.presentation.models.PlaylistPlaylistsPresentationModel
import com.example.features.playlists.presentation.models.SongPlaylistsPresentationModel

fun PlaylistPlaylistsDomainModel.toPlaylistPlaylistsPresentationModel(): PlaylistPlaylistsPresentationModel {

    return PlaylistPlaylistsPresentationModel(
        id = this.id,
        label = this.label,
        songs = this.songs.map { it.toSongPlaylistsPresentationModel() }
    )
}

fun SongPlaylistsDomainModel.toSongPlaylistsPresentationModel(): SongPlaylistsPresentationModel {

    return SongPlaylistsPresentationModel(
        id = this.id,
        playlistId = this.playlistId,
        displayName = this.displayName,
        duration = this.duration,
        author = this.author
    )
}

fun SongPlaylistsPresentationModel.toSongMusicSourceDomainModel(): SongMusicSourceDomainModel {

    return SongMusicSourceDomainModel(
        id = this.id
    )
}

fun SongPlaylistsPresentationModel.toSongInfoUIModel(): SongInfoUIModel {
    
    return SongInfoUIModel(
        id = this.id,
        name = this.displayName,
        duration = this.duration,
        artist = this.author
    )
}

fun PlaylistPlaylistsPresentationModel.toGridItem(
    label: String,
    action: (Long) -> Unit,
    action1: (Long) -> Unit
): GridItem.Item {

    return GridItem.Item(
        action = action,
        action1 = action1,
        itemId = this.id,
        label = label
    )
}