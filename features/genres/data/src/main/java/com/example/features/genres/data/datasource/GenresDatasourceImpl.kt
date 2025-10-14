package com.example.features.genres.data.datasource

import com.example.datasources.database.dao.GenreDao
import com.example.features.genres.data.mappers.toGenreEntity
import com.example.features.genres.data.mappers.toGenreGenresDomainModelEntity
import com.example.features.genres.domain.datasource.GenresDatasource
import com.example.features.genres.domain.model.GenreGenresDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GenresDatasourceImpl(
    private val genreDao: GenreDao
): GenresDatasource {

    override suspend fun insertGenres(genres: List<GenreGenresDomainModel/*.Entity*/>) {

        genreDao.insertGenres(
            genres = genres.map { it.toGenreEntity() }
        )
    }

    override suspend fun deleteGenres(genres: List<GenreGenresDomainModel/*.Entity*/>) {

        genreDao.deleteGenres(
            genres = genres.map { it.toGenreEntity() }
        )
    }

    override fun getAllGenres(): Flow<List<GenreGenresDomainModel/*.Entity*/>> {

        return genreDao.getAllGenres().map { genres ->

            genres.map { it.toGenreGenresDomainModelEntity() }
        }
    }

    /*override fun getAllGenresWithSongs(): Flow<List<GenreGenresDomainModel.Info>> {

        return genreDao.getAllGenresWithSongs().map { genres ->

            genres.map { it.toGenreGenresDomainModelInfo() }
        }
    }*/
}