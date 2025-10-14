package com.example.datasources.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SongDao {

    @Insert(
        entity = SongEntity::class,
        onConflict = ABORT
    )
    suspend fun insertSongs(songs: List<SongEntity>)

    @Delete(
        entity = SongEntity::class
    )
    suspend fun deleteSongs(songs: List<SongEntity>)

    @Update(
        entity = SongEntity::class,
        onConflict = REPLACE
    )
    suspend fun updateSongs(songs: List<SongEntity>)

    @Query(
        value = """
            SELECT *
            FROM songs
        """
    )
    fun getAllSongs(): Flow<List<SongEntity>>

    @Query(
        value = """
            SELECT s.*, a.name as name
            FROM songs s JOIN artists a on a.id = s.artist_id
        """
    )
    fun getAllSongsWithArtists(): Flow<List<SongWithArtist>>

    data class SongWithArtist(
        @Embedded
        val song: SongEntity,
        @ColumnInfo(name = "name")
        val artist: String
    ) {
    }

}