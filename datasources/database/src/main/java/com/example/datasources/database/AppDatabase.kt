package com.example.datasources.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.datasources.database.dao.PlaylistsDao
import com.example.datasources.database.dao.SongDao
import com.example.datasources.database.dao.entities.PlaylistEntity
import com.example.datasources.database.dao.entities.PlaylistSongEntity
import com.example.datasources.database.dao.entities.SongEntity

@Database(
    entities = [
        PlaylistEntity::class,
        PlaylistSongEntity::class,
        SongEntity::class
    ],
    version = 1
)
abstract class AppDatabase: RoomDatabase() {

    abstract fun songDao(): SongDao

    abstract fun playlistsDao(): PlaylistsDao
}