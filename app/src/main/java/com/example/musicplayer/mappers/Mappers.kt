package com.example.musicplayer.mappers

import androidx.media3.common.MediaItem
import com.example.core.ui.grid.model.GridItem
import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.presentation.models.PlaylistPlaylistsPresentationModel
import com.example.features.songs.domain.model.SongSongsDomainModel
import com.example.musicplayer.models.MusicSourceMainPresentationModel
import com.example.musicplayer.models.PlayerStateMainPresentationModel
import com.example.musicplayer.models.PlaylistInfoMainPresentationModel
import com.example.musicplayer.models.SongIDMainPresentationModel
import com.example.musicplayer.models.SongMainPresentationModel

fun PlaylistPlaylistsDomainModel.toPlaylistInfoMainPresentationModel(): PlaylistInfoMainPresentationModel {

    return PlaylistInfoMainPresentationModel(
        id = this.id,
        label = this.label
    )
}

fun MediaItem.toSongMainPresentationModel(current: Boolean, isUpNext: Boolean): SongMainPresentationModel {

    return SongMainPresentationModel(
        id = this.mediaId.toLong(),
        name = this.mediaMetadata.title.toString(),
        duration = this.mediaMetadata.durationMs?.toInt()?:0,
        author = this.mediaMetadata.artist.toString(),
        current = current,
        isUpNext = isUpNext
    )
}

fun MusicSourceMusicSourceDomainModel.toMusicSourceMainPresentationModel(mediaItems: List<MediaItem>): MusicSourceMainPresentationModel {

    return when (this) {
        is MusicSourceMusicSourceDomainModel.Source -> {
            MusicSourceMainPresentationModel.MusicSource(
                selectedIndex = this.initialIndex,
                displayText = this.displayText,
                position = 0L,
                songs = mediaItems,/*.map { it.id }.toSet()*/
                fromPrefs = false,
                //userSelected = true
            )
        }

        is MusicSourceMusicSourceDomainModel.None -> MusicSourceMainPresentationModel.Default
    }
}

fun PlayerStateMainPresentationModel.toMusicSourceMainPresentationModel(mediaItems: List<MediaItem>): MusicSourceMainPresentationModel.MusicSource {

    return MusicSourceMainPresentationModel.MusicSource(
        selectedIndex = this.currentIndex,
        displayText = this.displayText,
        position = this.position,
        songs = mediaItems,/*.map { it.id }.toSet()*/
        fromPrefs = false,
        //userSelected = false
    )
}

fun SongSongsDomainModel.toSongIDMainPresentationModel(): SongIDMainPresentationModel {
    
    return SongIDMainPresentationModel(
        id = this.id,
        msId = this.msId
    )
}
