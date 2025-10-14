package com.example.features.genres.domain.datasource

import com.example.features.genres.domain.model.GenreGenresDomainModel
import kotlinx.coroutines.flow.Flow

interface GenresDatasource {

    suspend fun insertGenres(genres: List<GenreGenresDomainModel/*.Entity*/>)

    suspend fun deleteGenres(genres: List<GenreGenresDomainModel/*.Entity*/>)

    fun getAllGenres(): Flow<List<GenreGenresDomainModel/*.Entity*/>>

    //fun getAllGenresWithSongs(): Flow<List<GenreGenresDomainModel.Info>>
}