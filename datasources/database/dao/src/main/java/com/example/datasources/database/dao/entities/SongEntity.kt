package com.example.datasources.database.dao.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Long,
    @ColumnInfo(name = "display_name")
    val displayName: String,
    @ColumnInfo(name = "duration")
    val duration: Int,
    @ColumnInfo(name = "author")
    val author: String,
    @ColumnInfo(name = "album")
    val album: Long,
    @ColumnInfo(name = "date_added")
    val dateAdded: Long
) {
}