package com.example.features.artists.presentation.mappers

import android.view.View
import com.example.core.ui.grid.model.GridItem
import com.example.core.ui.model.SongInfoUIModel
import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import com.example.features.artists.domain.model.ArtistArtistsDomainModel
import com.example.features.artists.presentation.model.AlbumArtistsPresentationModel
import com.example.features.artists.presentation.model.ArtistArtistsPresentationModel
import com.example.features.artists.presentation.model.PlaylistInfoArtistsPresentationModel
import com.example.features.artists.presentation.model.SongArtistsPresentationModel
import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel

fun ArtistArtistsDomainModel.toArtistArtistsPresentationModel(
    albums: List<AlbumArtistsPresentationModel>,
    songs: List<SongArtistsPresentationModel>
): ArtistArtistsPresentationModel {

    return ArtistArtistsPresentationModel(
        id = this.id,
        name = this.name,
        albumId = songs.firstOrNull()?.albumId?: -1L,
        albums = albums,
        songs = songs
    )
}

fun AlbumAlbumsDomainModel.toAlbumArtistsPresentationModel(songs: List<SongSongsDomainModel.Info>): AlbumArtistsPresentationModel {

    return AlbumArtistsPresentationModel(
        id = this.id,
        name = this.name,
        songs = songs.map { it.toSongArtistsPresentationModel() }
    )
}

fun SongSongsDomainModel.Info.toSongArtistsPresentationModel(): SongArtistsPresentationModel {

    return SongArtistsPresentationModel(
        id = this.id,
        msId = this.msId,
        albumId = this.albumId,
        displayName = this.name,
        artist = this.artist,
        duration = this.duration
    )
}

fun AlbumArtistsPresentationModel.toMusicSource(initialIndex: Int): MusicSourceMusicSourceDomainModel {

    return MusicSourceMusicSourceDomainModel.Source(
        initialIndex = initialIndex,
        displayText = this.name,
        songs = this.songs.map { it.msId }
    )
}

fun ArtistArtistsPresentationModel.toMusicSource(initialIndex: Int): MusicSourceMusicSourceDomainModel {

    return MusicSourceMusicSourceDomainModel.Source(
        initialIndex = initialIndex,
        displayText = this.name,
        songs = this.songs.map { it.msId }
    )
}

fun ArtistArtistsPresentationModel.toGridItem(
    action: (Long) -> Unit
): GridItem.Item {

    return GridItem.Item(
        itemId = this.id,
        label = this.name,
        albumId = this.albumId,
        action = action
    )
}

fun AlbumArtistsPresentationModel.toGridItem(
    action: (Long) -> Unit,
    actionAll: (Long) -> Unit
): GridItem.Item {

    return GridItem.Item(
        itemId = this.id,
        label = this.name,
        action = action,
        actionAll = actionAll
    )
}

fun SongArtistsPresentationModel.toGridItem(
    action: (Long) -> Unit,
    actionAll: (Long, View) -> Unit,
): GridItem.SongItem {

    return GridItem.SongItem(
        itemId = this.msId,
        albumId = this.albumId,
        action = action,
        actionAll = actionAll,
        title = this.displayName,
        duration = this.duration,
        artist = this.artist
    )
}

fun SongArtistsPresentationModel.toSongInfoUIModel(): SongInfoUIModel {

    return SongInfoUIModel(
        id = this.msId,
        albumId = this.albumId,
        name = this.displayName,
        duration = this.duration,
        artist = this.artist
    )
}

fun PlaylistPlaylistsDomainModel.toPlaylistInfoPresentationModel(): PlaylistInfoArtistsPresentationModel {

    return PlaylistInfoArtistsPresentationModel(
        id = this.id,
        label = this.label
    )
}
