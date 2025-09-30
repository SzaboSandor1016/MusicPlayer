package com.example.features.songs.domain.usecase

import com.example.features.songs.domain.model.SongMetadataSongsDomainModel
import com.example.features.songs.domain.repository.SongsRepository

class GetSongMetadataByIdSyncUseCase(
    private val songsRepository: SongsRepository
) {

    operator fun invoke(id: Long): SongMetadataSongsDomainModel? {

        return songsRepository.getSongMetadataFromIdSync(id)
    }
}