package com.example.datasources.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Relation
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Insert(
        entity = ArtistEntity::class,
        onConflict = REPLACE
    )
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Delete(
        entity = ArtistEntity::class
    )
    suspend fun deleteArtists(artists: List<ArtistEntity>)

    @Query(
        value = """
            SELECT *
            FROM artists
        """
    )
    fun getAllArtists(): Flow<List<ArtistEntity>>

    /*@Query(
        value = """
            SELECT *
            FROM artists
        """
    )
    fun getAllArtistsWithSongs(): Flow<List<ArtistWithSongs>>

    data class ArtistWithSongs(
        @Embedded
        val artist: ArtistEntity,
        @Relation(
            parentColumn = "id",
            entityColumn = "artist_id"
        )
        val songs: List<SongEntity>
    )*/
}