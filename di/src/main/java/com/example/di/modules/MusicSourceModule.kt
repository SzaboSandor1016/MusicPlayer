package com.example.di.modules

import com.example.features.musicsource.data.repository.MusicSourceRepositoryImpl
import com.example.features.musicsource.domain.repository.MusicSourceRepository
import com.example.features.musicsource.domain.usecases.AddQueuedUseCase
import com.example.features.musicsource.domain.usecases.AddUpNextUseCase
import com.example.features.musicsource.domain.usecases.GetAddQueuedUseCase
import com.example.features.musicsource.domain.usecases.GetAddUpNextUseCase
import com.example.features.musicsource.domain.usecases.GetMusicSourceUseCase
import com.example.features.musicsource.domain.usecases.SetMusicSourceUseCase
import org.koin.dsl.module

val musicSourceModule = module {

    single<MusicSourceRepository> { MusicSourceRepositoryImpl() }

    factory { GetMusicSourceUseCase(get()) }
    factory { SetMusicSourceUseCase(get()) }

    factory { AddUpNextUseCase(get()) }
    factory { GetAddUpNextUseCase(get()) }

    factory { AddQueuedUseCase(get()) }
    factory { GetAddQueuedUseCase(get()) }
}