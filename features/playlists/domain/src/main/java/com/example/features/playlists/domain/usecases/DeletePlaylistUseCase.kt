package com.example.features.playlists.domain.usecases

import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository

class DeletePlaylistUseCase(
    private val playlistsRepository: PlaylistsRepository
) {

    suspend operator fun invoke(playlistId: Long) {

        playlistsRepository.deletePlaylist(
            playlistId = playlistId
        )
    }
}