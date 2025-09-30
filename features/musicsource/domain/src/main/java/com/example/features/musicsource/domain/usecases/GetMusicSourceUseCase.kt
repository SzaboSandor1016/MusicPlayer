package com.example.features.musicsource.domain.usecases

import com.example.features.musicsource.domain.models.MusicSourceMusicSourceDomainModel
import com.example.features.musicsource.domain.repository.MusicSourceRepository
import kotlinx.coroutines.flow.Flow

class GetMusicSourceUseCase(
    private val musicSourceRepository: MusicSourceRepository
) {

    operator fun invoke(): Flow<MusicSourceMusicSourceDomainModel> {

        return musicSourceRepository.getMusicSource()
    }
}