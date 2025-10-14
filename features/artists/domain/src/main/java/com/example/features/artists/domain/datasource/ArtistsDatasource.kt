package com.example.features.artists.domain.datasource

import com.example.features.artists.domain.model.ArtistArtistsDomainModel
import kotlinx.coroutines.flow.Flow

interface ArtistsDatasource {

    suspend fun insertArtists(artists: List<ArtistArtistsDomainModel/*.Entity*/>)

    suspend fun deleteArtists(artists: List<ArtistArtistsDomainModel/*.Entity*/>)

    fun getAllArtists(): Flow<List<ArtistArtistsDomainModel/*.Entity*/>>

    //fun getAllArtistsWithAlbumsAndSongs(): Flow<List<ArtistArtistsDomainModel.Info>>
}