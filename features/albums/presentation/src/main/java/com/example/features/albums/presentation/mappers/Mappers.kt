package com.example.features.albums.presentation.mappers

import com.example.core.ui.grid.model.GridItem
import com.example.core.ui.model.SongInfoUIModel
import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import com.example.features.albums.domain.model.SongAlbumsDomainModel
import com.example.features.albums.presentation.model.AlbumAlbumsPresentationModel
import com.example.features.albums.presentation.model.PlaylistInfoAlbumsPresentationModel
import com.example.features.albums.presentation.model.SongAlbumsPresentationModel
import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel

fun AlbumAlbumsDomainModel.toAlbumAlbumsPresentationModel(
    songs: List<SongSongsDomainModel.Info>
): AlbumAlbumsPresentationModel {

    return AlbumAlbumsPresentationModel(
        id = this.id,
        name = this.name,
        songs = songs.map { it.toSongAlbumPresentationModel() }
    )
}

fun SongSongsDomainModel.Info.toSongAlbumPresentationModel(): SongAlbumsPresentationModel {

    return SongAlbumsPresentationModel(
        id = this.id,
        msId = this.msId,
        displayName = this.name,
        artist = this.artist,
        duration = this.duration
    )
}

fun AlbumAlbumsPresentationModel.toMusicSource(initialIndex: Int): MusicSourceMusicSourceDomainModel.Source {

    return MusicSourceMusicSourceDomainModel.Source(
        initialIndex = initialIndex,
        displayText = this.name,
        songs = this.songs.map { it.msId }
    )
}

fun AlbumAlbumsPresentationModel.toGridItem(
    action: (Long) -> Unit,
    actionAll: (Long) -> Unit
    ): GridItem.Item {

    return GridItem.Item(
        action = action,
        actionAll = actionAll,
        itemId = this.id,
        label = this.name
    )
}

fun SongAlbumsPresentationModel.toSongInfoUIModel(): SongInfoUIModel {

    return SongInfoUIModel(
        id = this.msId,
        name = this.displayName,
        duration = this.duration,
        artist = this.artist
    )
}

fun PlaylistPlaylistsDomainModel.toPlaylistInfoPresentationModel(): PlaylistInfoAlbumsPresentationModel {

    return PlaylistInfoAlbumsPresentationModel(
        id = this.id,
        label = this.label
    )
}