package com.example.features.musicsource.domain.usecases

import com.example.features.musicsource.domain.repository.MusicSourceRepository
import kotlinx.coroutines.flow.Flow

class GetAddQueuedUseCase(
    private val musicSourceRepository: MusicSourceRepository
) {

    operator fun invoke(): Flow<Long> {

        return musicSourceRepository.getAddQueued()
    }
}