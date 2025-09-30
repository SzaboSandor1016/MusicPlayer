package com.example.features.playlists.domain.usecases

import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository

class UpdatePlaylistSongsUseCase(
    private val playlistsRepository: PlaylistsRepository
) {

    suspend operator fun invoke(playlistSongs: List<PlaylistSongPlaylistsDomainModel>) {

        val updated = playlistSongs.mapIndexed { index, model ->

            model.copy(
                order = index + 1
            )
        }

        playlistsRepository.updatePlaylistSongs(
            playlistSongs = updated
        )
    }
}