package com.example.features.songs.domain.usecase

import com.example.features.songs.domain.model.SongSongsDomainModel
import com.example.features.songs.domain.repository.SongsRepository
import kotlinx.coroutines.flow.Flow

class GetAllSongsFromRoomUseCase(
    private val songsRepository: SongsRepository
) {

    operator fun invoke(): Flow<List<SongSongsDomainModel.Info>> {

        return songsRepository.getAllSongsInfoFromRoom()
    }
}