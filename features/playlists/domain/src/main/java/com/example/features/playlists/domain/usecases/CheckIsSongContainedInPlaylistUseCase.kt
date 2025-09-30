package com.example.features.playlists.domain.usecases

import com.example.features.playlists.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class CheckIsSongContainedInPlaylistUseCase(
    private val playlistsRepository: PlaylistsRepository
) {

    operator fun invoke(playlistId: Long, songId: Long): Flow<Boolean> {

        return playlistsRepository.isSongContainedInPlaylist(
            playlistId = playlistId,
            songId = songId
        )
    }
}