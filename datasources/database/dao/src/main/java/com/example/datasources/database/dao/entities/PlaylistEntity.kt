package com.example.datasources.database.dao.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "label")
    val label: String,
    @ColumnInfo(name = "type")
    val type: Int
) {
}