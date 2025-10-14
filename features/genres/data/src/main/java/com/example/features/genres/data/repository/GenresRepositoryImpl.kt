package com.example.features.genres.data.repository

import com.example.datasources.mediastore.domain.MediaStoreLocalDatasource
import com.example.features.genres.data.mappers.toGenreGenresDomainModelEntity
import com.example.features.genres.domain.datasource.GenresDatasource
import com.example.features.genres.domain.model.GenreGenresDomainModel
import com.example.features.genres.domain.repository.GenresRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GenresRepositoryImpl(
    private val genresDatasource: GenresDatasource,
    private val mediaStoreLocalDatasource: MediaStoreLocalDatasource
): GenresRepository {

    override suspend fun insertGenres(genres: List<GenreGenresDomainModel/*.Entity*/>) {

        genresDatasource.insertGenres(genres)
    }

    override suspend fun deleteGenres(genres: List<GenreGenresDomainModel/*.Entity*/>) {

        genresDatasource.deleteGenres(genres)
    }

    override fun getAllGenresFromRoom(): Flow<List<GenreGenresDomainModel/*.Entity*/>> {

        return genresDatasource.getAllGenres()
    }

    override fun getAllGenresFromMediaStore(): Flow<List<GenreGenresDomainModel/*.Entity*/>> {

        return mediaStoreLocalDatasource.getListOfGenres().map { genres ->

            genres.map { it.toGenreGenresDomainModelEntity() }
        }
    }

    /*override fun getAllGenresWithSongs(): Flow<List<GenreGenresDomainModel.Info>> {

        return genresDatasource.getAllGenresWithSongs()
    }*/
}