package com.example.features.artists.domain.usecase

import com.example.features.artists.domain.model.ArtistArtistsDomainModel
import com.example.features.artists.domain.repository.ArtistsRepository
import kotlinx.coroutines.flow.Flow

class GetAllArtistsUseCase(
    private val artistsRepository: ArtistsRepository
) {

    operator fun invoke(): Flow<List<ArtistArtistsDomainModel/*.Info*/>> {

        return artistsRepository.getAllArtistsFromRoom()
    }
}