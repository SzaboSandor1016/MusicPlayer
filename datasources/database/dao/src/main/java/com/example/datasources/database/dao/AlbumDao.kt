package com.example.datasources.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.example.datasources.database.dao.entities.AlbumEntity
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(
        entity = AlbumEntity::class,
        onConflict = REPLACE
    )
    suspend fun insertAlbums(albums: List<AlbumEntity>)

    @Delete(
        entity = AlbumEntity::class
    )
    suspend fun deleteAlbums(albums: List<AlbumEntity>)

    @Query(
        value = """
            SELECT *
            FROM albums
        """
    )
    fun getAllAlbums(): Flow<List<AlbumEntity>>

    /*@Transaction
    @Query(
        value = """
            SELECT *
            FROM albums
            WHERE id in(:albumIds)
        """
    )
    fun getAllAlbumsById(albumIds: List<Long>): Flow<List<AlbumWithSongs>>

    @Transaction
    @Query(
        value = """
            SELECT *
            FROM albums
        """
    )
    fun getAllAlbumsWithSongs(): Flow<List<AlbumWithSongs>>

    data class AlbumWithSongs(
        @Embedded
        val album: AlbumEntity,
        @Relation(
            entity = SongEntity::class,
            parentColumn = "id",
            entityColumn = "album_id"
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