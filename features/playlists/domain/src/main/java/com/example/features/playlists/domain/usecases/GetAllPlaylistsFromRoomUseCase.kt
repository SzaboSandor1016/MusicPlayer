package com.example.features.playlists.domain.usecases

import com.example.features.playlists.domain.models.PlaylistPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class GetAllPlaylistsFromRoomUseCase(
    private val playlistsRepository: PlaylistsRepository
) {

    operator fun invoke(): Flow<List<PlaylistPlaylistsDomainModel/*.Info*/>> {

        return playlistsRepository.getAllPlaylistsFromRoom()
    }
}