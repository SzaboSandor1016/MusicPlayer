package com.example.features.playlists.domain.usecases

import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository

class InsertNewPlaylistUseCase(
    private val playlistsRepository: PlaylistsRepository
) {

    suspend operator fun invoke(playlistName: String, playlistType: Int) {

        val playlist = PlaylistPlaylistsDomainModel(
            id = 0,
            label = playlistName,
            type = playlistType,
            songs = emptyList()
        )

        playlistsRepository.insertNewPlaylist(
            playlist = playlist
        )
    }
}