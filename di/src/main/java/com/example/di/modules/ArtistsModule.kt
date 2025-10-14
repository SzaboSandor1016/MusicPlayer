package com.example.di.modules

import com.example.features.artists.data.datasource.ArtistsDatasourceImpl
import com.example.features.artists.data.repository.ArtistsRepositoryImpl
import com.example.features.artists.domain.datasource.ArtistsDatasource
import com.example.features.artists.domain.repository.ArtistsRepository
import com.example.features.artists.domain.usecase.GetAllArtistsUseCase
import com.example.features.artists.presentation.viewmodel.ViewModelArtists
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val artistsModule = module {

    single<ArtistsDatasource> { ArtistsDatasourceImpl(get()) }
    single<ArtistsRepository> { ArtistsRepositoryImpl(get(), get()) }

    factory { GetAllArtistsUseCase(get()) }

    viewModelOf(::ViewModelArtists)
}