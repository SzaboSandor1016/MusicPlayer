package com.example.features.player.presentation.mappers

import androidx.media3.common.MediaItem
import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.songs.domain.model.SongSongsDomainModel
import com.example.features.player.presentation.model.MusicSourcePlayerPresentationModel
import com.example.features.player.presentation.model.PlayerStatePlayerPresentationModel
import com.example.features.player.presentation.model.PlaylistInfoPlayerPresentationModel
import com.example.features.player.presentation.model.SongIDPlayerPresentationModel
import com.example.features.player.presentation.model.SongPlayerPresentationModel

fun PlaylistPlaylistsDomainModel.toPlaylistInfoMainPresentationModel(): PlaylistInfoPlayerPresentationModel {

    return PlaylistInfoPlayerPresentationModel(
        id = this.id,
        label = this.label
    )
}

fun MediaItem.toSongMainPresentationModel(current: Boolean, isUpNext: Boolean): SongPlayerPresentationModel {

    return SongPlayerPresentationModel(
        id = this.mediaId.toLong(),
        albumArtworkUri = this.mediaMetadata.artworkUri,
        name = this.mediaMetadata.title.toString(),
        duration = this.mediaMetadata.durationMs?.toInt()?:0,
        author = this.mediaMetadata.artist.toString(),
        current = current,
        isUpNext = isUpNext
    )
}

fun MusicSourceMusicSourceDomainModel.toMusicSourceMainPresentationModel(mediaItems: List<MediaItem>): MusicSourcePlayerPresentationModel {

    return when (this) {
        is MusicSourceMusicSourceDomainModel.Source -> {
            MusicSourcePlayerPresentationModel.MusicSource(
                selectedIndex = this.initialIndex,
                displayText = this.displayText,
                position = 0L,
                songs = mediaItems,/*.map { it.id }.toSet()*/
                fromPrefs = false,
                //userSelected = true
            )
        }

        is MusicSourceMusicSourceDomainModel.None -> MusicSourcePlayerPresentationModel.Default
    }
}

fun PlayerStatePlayerPresentationModel.toMusicSourceMainPresentationModel(mediaItems: List<MediaItem>): MusicSourcePlayerPresentationModel.MusicSource {

    return MusicSourcePlayerPresentationModel.MusicSource(
        selectedIndex = this.currentIndex,
        displayText = this.displayText,
        position = this.position,
        songs = mediaItems,/*.map { it.id }.toSet()*/
        fromPrefs = false,
        //userSelected = false
    )
}

fun SongSongsDomainModel.Info.toSongIDMainPresentationModel(): SongIDPlayerPresentationModel {
    
    return SongIDPlayerPresentationModel(
        id = this.id,
        msId = this.msId
    )
}
