package com.example.features.playlists.domain.usecases

import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.first

class InsertPlaylistSongUseCase(
    private val playlistsRepository: PlaylistsRepository
) {

    suspend operator fun invoke(playlistId: Long, songId: Long) {

        //val playlist = playlistsRepository.getPlaylistById(playlistId).first()

        val playlistSongs = playlistsRepository.getPlaylistSongsByPlaylistId(playlistId).first()

        playlistsRepository.insertPlaylistSong(
            playlistSong = PlaylistSongPlaylistsDomainModel(
                playlistId = playlistId,
                songId = songId,
                order = playlistSongs.size + 1
            )
        )
    }
}