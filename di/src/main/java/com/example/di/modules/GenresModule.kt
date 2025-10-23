package com.example.di.modules

import com.example.features.genres.data.datasource.GenresDatasourceImpl
import com.example.features.genres.data.repository.GenresRepositoryImpl
import com.example.features.genres.domain.datasource.GenresDatasource
import com.example.features.genres.domain.repository.GenresRepository
import com.example.features.genres.domain.usecase.GetAllGenresUseCase
import com.example.features.genres.presentation.viewmodel.ViewModelGenres
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val genresModule = module {

    single<GenresDatasource> { GenresDatasourceImpl(get()) }
    single<GenresRepository> { GenresRepositoryImpl(get(), get()) }

    factory { GetAllGenresUseCase(get()) }

    viewModelOf(::ViewModelGenres)
}