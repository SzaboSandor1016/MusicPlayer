package com.example.features.genres.domain.usecase

import com.example.features.genres.domain.model.GenreGenresDomainModel
import com.example.features.genres.domain.repository.GenresRepository
import kotlinx.coroutines.flow.Flow

class GetAllGenresUseCase(
    private val genresRepository: GenresRepository
) {

    operator fun invoke(): Flow<List<GenreGenresDomainModel/*.Info*/>> {

        return genresRepository.getAllGenresFromRoom()
    }
}