package com.example.features.artists.domain.repository

import com.example.features.artists.domain.model.ArtistArtistsDomainModel
import kotlinx.coroutines.flow.Flow

interface ArtistsRepository {

    suspend fun insertArtists(artists: List<ArtistArtistsDomainModel/*.Entity*/>)

    suspend fun deleteArtists(artists: List<ArtistArtistsDomainModel/*.Entity*/>)

    fun getAllArtistsFromRoom(): Flow<List<ArtistArtistsDomainModel/*.Entity*/>>

    fun getAllArtistsFromMediaStore(): Flow<List<ArtistArtistsDomainModel/*.Entity*/>>

    //fun getAllArtistsWithAlbumsAndSongs(): Flow<List<ArtistArtistsDomainModel.Info>>
}