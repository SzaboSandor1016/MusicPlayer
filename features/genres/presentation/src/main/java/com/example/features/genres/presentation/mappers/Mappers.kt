package com.example.features.genres.presentation.mappers

import com.example.core.ui.grid.model.GridItem
import com.example.core.ui.model.SongInfoUIModel
import com.example.features.genres.domain.model.GenreGenresDomainModel
import com.example.features.genres.presentation.model.GenreGenresPresentationModel
import com.example.features.genres.presentation.model.PlaylistInfoGenresPresentationModel
import com.example.features.genres.presentation.model.SongGenresPresentationModel
import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel

fun GenreGenresDomainModel.toGenreGenresPresentationModel(songs: List<SongGenresPresentationModel>): GenreGenresPresentationModel {

    return GenreGenresPresentationModel(
        id = this.id,
        albumId = songs.firstOrNull()?.albumId?: -1L,
        name = this.name,
        songs = songs
    )
}

fun SongSongsDomainModel.Info.toSongGenresPresentationModel(): SongGenresPresentationModel {

    return SongGenresPresentationModel(
        id = this.id,
        msId = this.msId,
        albumId = this.albumId,
        displayName = this.name,
        artist = this.artist,
        duration = this.duration
    )
}

fun GenreGenresPresentationModel.toMusicSource(initialIndex: Int): MusicSourceMusicSourceDomainModel.Source {

    return MusicSourceMusicSourceDomainModel.Source(
        initialIndex = initialIndex,
        displayText = this.name,
        songs = this.songs.map { it.msId }
    )
}

fun GenreGenresPresentationModel.toGridItem(
    action: (Long) -> Unit,
    actionAll: (Long) -> Unit
): GridItem.Item {

    return GridItem.Item(
        action = action,
        actionAll = actionAll,
        itemId = this.id,
        albumId = this.albumId,
        label = this.name
    )
}

fun SongGenresPresentationModel.toSongInfoUIModel(): SongInfoUIModel {

    return SongInfoUIModel(
        id = this.msId,
        albumId = this.albumId,
        name = this.displayName,
        duration = this.duration,
        artist = this.artist
    )
}

fun PlaylistPlaylistsDomainModel.toPlaylistInfoPresentationModel(): PlaylistInfoGenresPresentationModel {

    return PlaylistInfoGenresPresentationModel(
        id = this.id,
        label = this.label
    )
}