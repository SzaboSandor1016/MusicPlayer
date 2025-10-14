package com.example.features.albums.domain.usecase

import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import com.example.features.albums.domain.repository.AlbumsRepository
import kotlinx.coroutines.flow.Flow

class GetAllAlbumsUseCase(
    private val albumsRepository: AlbumsRepository
) {

    operator fun invoke(): Flow<List<AlbumAlbumsDomainModel>> {

        return albumsRepository.getAllAlbumsFromRoom()
    }
}