package com.example.datasources.database.dao.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "ms_id")
    val msId: Long,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "duration")
    val duration: Int,
    @ColumnInfo(name = "album_id")
    val albumId: Long,
    @ColumnInfo(name = "artist_id")
    val artistId: Long,
    @ColumnInfo(name = "genre_id")
    val genreId: Long,
    @ColumnInfo(name = "date_added")
    val dateAdded: Long,
    @ColumnInfo(name = "key")
    val key: String
) {
}