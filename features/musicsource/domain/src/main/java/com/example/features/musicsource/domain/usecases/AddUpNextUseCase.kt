package com.example.features.musicsource.domain.usecases

import com.example.features.musicsource.domain.repository.MusicSourceRepository

class AddUpNextUseCase(
    private val musicSourceRepository: MusicSourceRepository
) {

    suspend operator fun invoke(songId: Long) {

        musicSourceRepository.addUpNext(songId)
    }
}