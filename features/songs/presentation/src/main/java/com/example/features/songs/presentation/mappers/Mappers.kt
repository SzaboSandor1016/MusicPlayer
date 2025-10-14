package com.example.features.songs.presentation.mappers

import com.example.core.ui.model.SongInfoUIModel
import com.example.features.musicsource.domain.models.SongMusicSourceDomainModel
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel
import com.example.features.songs.presentation.models.PlaylistInfoSongsPresentationModel
import com.example.features.songs.presentation.models.SongSongsPresentationModel

fun SongSongsDomainModel.Info.toSongSongsPresentationModel(): SongSongsPresentationModel {

    return SongSongsPresentationModel(
        id = this.id,
        msId = this.msId,
        name = this.name,
        duration = this.duration,
        author = this.artist
    )
}

fun SongSongsPresentationModel.toSongMusicSourceDomainModel(): SongMusicSourceDomainModel {

    return SongMusicSourceDomainModel(
        id = this.id
    )
}

fun PlaylistPlaylistsDomainModel.toPlaylistInfoPresentationModel(): PlaylistInfoSongsPresentationModel {

    return PlaylistInfoSongsPresentationModel(
        id = this.id,
        label = this.label
    )
}

fun SongSongsPresentationModel.toSongInfoUIModel(): SongInfoUIModel {

    return SongInfoUIModel(
        id = this.msId,
        name = this.name,
        duration = this.duration,
        artist = this.author
    )
}