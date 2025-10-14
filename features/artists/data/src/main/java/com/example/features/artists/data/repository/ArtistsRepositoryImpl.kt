package com.example.features.artists.data.repository

import com.example.datasources.mediastore.domain.MediaStoreLocalDatasource
import com.example.features.artists.data.mappers.toArtistArtistsDomainModelEntity
import com.example.features.artists.domain.datasource.ArtistsDatasource
import com.example.features.artists.domain.model.ArtistArtistsDomainModel
import com.example.features.artists.domain.repository.ArtistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ArtistsRepositoryImpl(
    private val artistsDatasource: ArtistsDatasource,
    private val mediaStoreLocalDatasource: MediaStoreLocalDatasource
): ArtistsRepository {

    override suspend fun insertArtists(artists: List<ArtistArtistsDomainModel/*.Entity*/>) {

        artistsDatasource.insertArtists(artists)
    }

    override suspend fun deleteArtists(artists: List<ArtistArtistsDomainModel/*.Entity*/>) {

        artistsDatasource.deleteArtists(artists)
    }

    override fun getAllArtistsFromRoom(): Flow<List<ArtistArtistsDomainModel/*.Entity*/>> {

        return artistsDatasource.getAllArtists()
    }

    override fun getAllArtistsFromMediaStore(): Flow<List<ArtistArtistsDomainModel/*.Entity*/>> {

        return mediaStoreLocalDatasource.getListOfArtists().map { artists ->

            artists.map { it.toArtistArtistsDomainModelEntity() }
        }
    }

    /*override fun getAllArtistsWithAlbumsAndSongs(): Flow<List<ArtistArtistsDomainModel.Info>> {

        return artistsDatasource.getAllArtistsWithAlbumsAndSongs()
    }*/
}