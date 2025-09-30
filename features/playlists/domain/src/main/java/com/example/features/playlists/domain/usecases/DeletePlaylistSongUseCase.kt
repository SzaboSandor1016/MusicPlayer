package com.example.features.playlists.domain.usecases

import com.example.features.playlists.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.first

class DeletePlaylistSongUseCase(
    private val playlistsRepository: PlaylistsRepository,
    private val updatePlaylistSongsUseCase: UpdatePlaylistSongsUseCase
) {

    suspend operator fun invoke(playlistId: Long, songId: Long) {

        playlistsRepository.deletePlaylistSong(
            playlistId = playlistId,
            songId = songId
        )

        val playlistSongs = playlistsRepository.getPlaylistSongsByPlaylistId(playlistId).first()

        updatePlaylistSongsUseCase(playlistSongs)
    }
}