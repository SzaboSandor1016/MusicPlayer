package com.example.di.modules

import com.example.features.playlists.data.datasource.PlaylistsRoomDatasourceImpl
import com.example.features.playlists.data.repository.PlaylistsRepositoryImpl
import com.example.features.playlists.domain.datasource.PlaylistsRoomDatasource
import com.example.features.playlists.domain.repository.PlaylistsRepository
import com.example.features.playlists.domain.usecases.CheckIsSongContainedInPlaylistUseCase
import com.example.features.playlists.domain.usecases.DeletePlaylistSongUseCase
import com.example.features.playlists.domain.usecases.DeletePlaylistUseCase
import com.example.features.playlists.domain.usecases.GetAllPlaylistsFromRoomUseCase
import com.example.features.playlists.domain.usecases.InsertNewPlaylistUseCase
import com.example.features.playlists.domain.usecases.InsertPlaylistSongUseCase
import com.example.features.playlists.domain.usecases.UpdatePlaylistSongsUseCase
import com.example.features.playlists.presentation.viewmodel.ViewModelPlaylists
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val playlistsModule = module {

    single<PlaylistsRoomDatasource> { PlaylistsRoomDatasourceImpl(get()) }

    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get()) }

    factory { DeletePlaylistUseCase(get()) }
    factory { DeletePlaylistSongUseCase(get(), get() ) }
    factory { GetAllPlaylistsFromRoomUseCase(get()) }
    factory { InsertNewPlaylistUseCase(get()) }
    factory { InsertPlaylistSongUseCase(get()) }
    factory { UpdatePlaylistSongsUseCase(get()) }
    factory { CheckIsSongContainedInPlaylistUseCase(get()) }
    viewModelOf(::ViewModelPlaylists)
}