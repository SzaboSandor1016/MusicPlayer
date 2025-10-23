package com.example.di.modules

import com.example.features.albums.data.datasource.AlbumsDatasourceImpl
import com.example.features.albums.data.repository.AlbumsRepositoryImpl
import com.example.features.albums.domain.datasource.AlbumsDatasource
import com.example.features.albums.domain.repository.AlbumsRepository
import com.example.features.albums.domain.usecase.GetAllAlbumsUseCase
import com.example.features.albums.presentation.viewmodel.ViewModelAlbums
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val albumsModule = module {

    single<AlbumsDatasource> { AlbumsDatasourceImpl(get()) }
    single<AlbumsRepository> { AlbumsRepositoryImpl(get(), get()) }

    factory { GetAllAlbumsUseCase(get()) }

    viewModelOf(::ViewModelAlbums)
}