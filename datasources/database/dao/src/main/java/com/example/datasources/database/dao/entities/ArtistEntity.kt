package com.example.datasources.database.dao.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long = 0,
    @ColumnInfo(name = "name")
    val name: String
) {
}