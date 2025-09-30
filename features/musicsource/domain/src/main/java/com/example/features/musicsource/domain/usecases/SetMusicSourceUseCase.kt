package com.example.features.musicsource.domain.usecases

import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.musicsource.domain.repository.MusicSourceRepository

class SetMusicSourceUseCase(
    private val musicSourceRepository: MusicSourceRepository
) {

    suspend operator fun invoke(source: MusicSourceMusicSourceDomainModel) {

        musicSourceRepository.setMusicSource(
            source = source
        )
    }
}