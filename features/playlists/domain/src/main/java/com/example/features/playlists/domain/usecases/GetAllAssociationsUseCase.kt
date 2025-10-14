package com.example.features.playlists.domain.usecases

import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow

class GetAllAssociationsUseCase(
    private val playlistsRepository: PlaylistsRepository
) {

    operator fun invoke(): Flow<List<PlaylistSongPlaylistsDomainModel>> {

        return playlistsRepository.getAllAssociations()
    }
}