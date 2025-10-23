package com.example.di.modules

import com.example.features.songs.data.datasource.SongsRoomDatasourceImpl
import com.example.features.songs.data.repository.SongsRepositoryImpl
import com.example.features.songs.domain.datasource.SongsRoomDatasource
import com.example.features.songs.domain.repository.SongsRepository
import com.example.features.songs.domain.usecase.AssembleSourceMediaItemsSyncUseCase
import com.example.features.songs.domain.usecase.AssembleSourceMediaItemsFlowUseCase
import com.example.features.songs.domain.usecase.GetAllSongsFromRoomUseCase
import com.example.features.songs.domain.usecase.GetSongMetadataByIdSyncUseCase
import com.example.features.songs.domain.usecase.GetSongMetadataByIdFlowUseCase
import com.example.features.songs.presentation.vievmodel.ViewModelSongs
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val songsModule = module {

    single<SongsRoomDatasource> {
        SongsRoomDatasourceImpl(get())
    }

    single<SongsRepository> { SongsRepositoryImpl(get(), get()) }

    factory { GetSongMetadataByIdFlowUseCase(get()) }
    factory { GetSongMetadataByIdSyncUseCase(get()) }
    /*factory { GetSongThumbnailByIdFlowUseCase(get()) }
    factory { GetSongThumbnailByIdSyncUseCase(get()) }
    factory { GetEmbeddedAlbumArtByIdFlowUseCase(get()) }
    factory { GetEmbeddedAlbumArtByIdSyncUseCase(get()) }*/
    factory { GetAllSongsFromRoomUseCase(get()) }
    factory { AssembleSourceMediaItemsFlowUseCase(
        get(),
        )
    }
    factory {
        AssembleSourceMediaItemsSyncUseCase(
            get(),
        )
    }

    viewModelOf(::ViewModelSongs)
}