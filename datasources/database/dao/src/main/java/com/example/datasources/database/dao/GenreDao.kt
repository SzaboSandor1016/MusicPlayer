package com.example.datasources.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.GenreEntity
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {

    @Insert(
        entity = GenreEntity::class,
        onConflict = REPLACE
    )
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Delete(
        entity = GenreEntity::class
    )
    suspend fun deleteGenres(genres: List<GenreEntity>)

    @Query(
        value = """
            SELECT * 
            FROM genres
        """
    )
    fun getAllGenres(): Flow<List<GenreEntity>>

    /*@Transaction
    @Query(
        value = """
            SELECT * 
            FROM genres
        """
    )
    fun getAllGenresWithSongs(): Flow<List<GenreWithSongs>>

    data class GenreWithSongs(
        @Embedded
        val genre: GenreEntity,
        @Relation(
            entity = SongEntity::class,
            entityColumn = "genre_id",
            parentColumn = "id"
        )
        val songs: List<SongWithArtist>
    )

    data class SongWithArtist(
        @Embedded
        val song: SongEntity,
        @Relation(
            entityColumn = "id",
            parentColumn = "artist_id"
        )
        val artist: ArtistEntity
    )*/
}