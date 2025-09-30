package com.example.features.songs.domain.usecase

import com.example.features.songs.domain.model.SongMetadataSongsDomainModel
import com.example.features.songs.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow

class GetSongMetadataByIdFlowUseCase(
    private val songsRepository: SongsRepository
) {

    operator fun invoke(id: Long): Flow<SongMetadataSongsDomainModel?> {

        return songsRepository.getSongMetadataFromIdFlow(id)
    }
}