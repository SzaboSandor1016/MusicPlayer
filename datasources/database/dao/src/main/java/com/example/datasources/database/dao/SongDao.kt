package com.example.datasources.database.dao

import androidx.room.Dao
import androidx.room.Delete
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
}