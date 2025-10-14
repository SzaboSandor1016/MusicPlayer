package com.example.features.artists.data.datasource

import com.example.datasources.database.dao.ArtistDao
import com.example.features.artists.data.mappers.toArtistArtistsDomainModelEntity
import com.example.features.artists.data.mappers.toArtistEntity
import com.example.features.artists.domain.datasource.ArtistsDatasource
import com.example.features.artists.domain.model.ArtistArtistsDomainModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArtistsDatasourceImpl(
    private val artistDao: ArtistDao
): ArtistsDatasource {

    override suspend fun insertArtists(artists: List<ArtistArtistsDomainModel/*.Entity*/>) {

        artistDao.insertArtists(
            artists = artists.map { it.toArtistEntity() }
        )
    }

    override suspend fun deleteArtists(artists: List<ArtistArtistsDomainModel/*.Entity*/>) {

        artistDao.deleteArtists(
            artists = artists.map { it.toArtistEntity() }
        )
    }

    override fun getAllArtists(): Flow<List<ArtistArtistsDomainModel/*.Entity*/>> {

        return artistDao.getAllArtists().map { artists ->

            artists.map { it.toArtistArtistsDomainModelEntity() }
        }
    }

    /*override fun getAllArtistsWithAlbumsAndSongs(): Flow<List<ArtistArtistsDomainModel.Info>> {

        return artistDao.getAllArtistsWithSongs().map { artists ->

            artists.map { it.toArtistArtistsDomainModelInfo() }
        }
    }*/
}