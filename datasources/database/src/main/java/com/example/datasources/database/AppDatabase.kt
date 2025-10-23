package com.example.datasources.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.datasources.database.dao.AlbumDao
import com.example.datasources.database.dao.ArtistDao
import com.example.datasources.database.dao.GenreDao
import com.example.datasources.database.dao.PlaylistsDao
import com.example.datasources.database.dao.SongDao
import com.example.datasources.database.dao.entities.AlbumEntity
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.GenreEntity
import com.example.datasources.database.dao.entities.PlaylistEntity
import com.example.datasources.database.dao.entities.PlaylistSongEntity
import com.example.datasources.database.dao.entities.SongEntity

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistSongEntity::class,
        SongEntity::class,
        ArtistEntity::class,
        AlbumEntity::class,
        GenreEntity::class
    ],
    /*autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],*/
    version = 1,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun songDao(): SongDao

    abstract fun playlistsDao(): PlaylistsDao

    abstract fun albumDao(): AlbumDao

    abstract fun artistDao(): ArtistDao

    abstract fun genreDao(): GenreDao
}