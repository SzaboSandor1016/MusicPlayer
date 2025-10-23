package com.example.musicplayer

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.datasources.database.AppDatabase
import com.example.datasources.database.dao.AlbumDao
import com.example.datasources.database.dao.ArtistDao
import com.example.datasources.database.dao.GenreDao
import com.example.datasources.database.dao.PlaylistsDao
import com.example.datasources.database.dao.SongDao
import com.example.di.modules.albumsModule
import com.example.di.modules.artistsModule
import com.example.di.modules.genresModule
import com.example.di.modules.mediaStoreModule
import com.example.di.modules.musicSourceModule
import com.example.di.modules.playerModule
import com.example.di.modules.playlistsModule
import com.example.di.modules.songsModule
import com.example.di.modules.syncModule
import com.example.features.player.presentation.sharedprefs.BassBoostVirtualizerPreferences
import com.example.features.player.presentation.sharedprefs.BassBoostVirtualizerPreferences.Companion.BASS_BOOST_VIRTUALIZER_PREFERENCES
import com.example.features.player.presentation.sharedprefs.EqualizerPreferences
import com.example.features.player.presentation.sharedprefs.EqualizerPreferences.Companion.AUDIO_EFFECT_PREFERENCES
import com.example.features.player.presentation.sharedprefs.PlayerPreferences
import com.example.features.player.presentation.sharedprefs.PlayerPreferences.Companion.PLAYER_PREFERENCES
import com.example.musicplayer.viewmodel.ViewModelMain
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

class Application: Application() {

    companion object {

        lateinit var appContext: Context
            private set

        lateinit var appDatabase: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext

        appDatabase = Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            name = "music_player_db"
        ).createFromAsset("database/music_player_db.db").build()

        val appModule = module {

            single(named("appContext")) {
                applicationContext
            }

            single(named("GSON")) {
                GsonBuilder().create()
            }

            single(named(AUDIO_EFFECT_PREFERENCES)) {

                applicationContext.getSharedPreferences(AUDIO_EFFECT_PREFERENCES, MODE_PRIVATE)
            }

            single(named(BASS_BOOST_VIRTUALIZER_PREFERENCES)) {

                applicationContext.getSharedPreferences(BASS_BOOST_VIRTUALIZER_PREFERENCES, MODE_PRIVATE)
            }

            single(named(PLAYER_PREFERENCES)) {

                applicationContext.getSharedPreferences(PLAYER_PREFERENCES, MODE_PRIVATE)
            }

            singleOf(::EqualizerPreferences)

            singleOf(::BassBoostVirtualizerPreferences)

            singleOf(::PlayerPreferences)

            viewModelOf(::ViewModelMain)
        }

        val databaseModule = module {

            single<SongDao> { appDatabase.songDao() }
            single<PlaylistsDao> { appDatabase.playlistsDao() }
            single<AlbumDao> { appDatabase.albumDao() }
            single<ArtistDao> { appDatabase.artistDao() }
            single<GenreDao> { appDatabase.genreDao() }
        }

        startKoin {

            androidLogger()

            androidContext(appContext)

            modules(
                appModule,
                databaseModule,
                genresModule,
                albumsModule,
                artistsModule,
                playerModule,
                playlistsModule,
                musicSourceModule,
                mediaStoreModule,
                playlistsModule,
                songsModule,
                syncModule
            )
        }
    }
}