package com.example.features.genres.domain.repository

import com.example.features.genres.domain.model.GenreGenresDomainModel
import kotlinx.coroutines.flow.Flow

interface GenresRepository {

    suspend fun insertGenres(genres: List<GenreGenresDomainModel/*.Entity*/>)

    suspend fun deleteGenres(genres: List<GenreGenresDomainModel/*.Entity*/>)

    fun getAllGenresFromRoom(): Flow<List<GenreGenresDomainModel/*.Entity*/>>

    fun getAllGenresFromMediaStore(): Flow<List<GenreGenresDomainModel/*.Entity*/>>

    //fun getAllGenresWithSongs(): Flow<List<GenreGenresDomainModel/*.Info*/>>
}