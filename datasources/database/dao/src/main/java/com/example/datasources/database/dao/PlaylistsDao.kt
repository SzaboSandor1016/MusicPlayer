package com.example.datasources.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.ABORT
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import androidx.room.Update
import com.example.datasources.database.dao.entities.PlaylistEntity
import com.example.datasources.database.dao.entities.PlaylistSongEntity
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {

    @Insert(
        entity = PlaylistEntity::class,
        onConflict = ABORT
    )
    suspend fun insertNewPlaylist(playlistEntity: PlaylistEntity)

    @Query(
        value = """
            DELETE FROM playlists
            WHERE id = :playlistId
        """
    )
    suspend fun deletePlaylist(playlistId: Long)

    @Insert(
        entity = PlaylistSongEntity::class,
        onConflict = ABORT
    )
    suspend fun insertNewPlaylistSong(playlistSongEntity: PlaylistSongEntity)

    @Insert(
        entity = PlaylistSongEntity::class,
        onConflict = ABORT
    )
    suspend fun insertNewPlaylistSongs(playlistSongEntities: List<PlaylistSongEntity>)

    @Update(
        entity = PlaylistSongEntity::class,
        onConflict = REPLACE
    )
    suspend fun updatePlaylistSongs(playlistSongs: List<PlaylistSongEntity>)

    @Query(
        value = """
            DELETE from associations
            WHERE playlist_id = :playlistId AND song_id = :songId
        """
    )
    suspend fun deletePlaylistSong(playlistId: Long, songId: Long)

    @Query(
        value = """
            SELECT *
            FROM associations
            WHERE playlist_id = :playlistId AND song_id = :songId
            LIMIT 1
        """
    )
    fun getAssociationByPlaylistAndSongId(playlistId: Long, songId: Long): Flow<PlaylistSongEntity?>

    @Query(
        value = """
            SELECT *
            FROM associations
        """
    )
    fun getAllAssociations(): Flow<List<PlaylistSongEntity>>

    @Query(
        """
            SELECT *
            FROM associations
            WHERE playlist_id = :playlistId
        """
    )
    fun getAssociationsByPlaylistId(playlistId: Long): Flow<List<PlaylistSongEntity>>

    @Query(
        value = """
            SELECT * 
            FROM playlists
            WHERE id = :playlistId
        """
    )
    fun getPlaylistById(playlistId: Long): Flow<PlaylistWithSongs>

    @Transaction
    @Query(
        value = """
            SELECT *
            FROM playlists
        """
    )
    fun getAllPlaylists(): Flow<List<PlaylistWithSongs>>

    data class PlaylistWithSongs(
        @Embedded
        val playlistEntity: PlaylistEntity,
        @Relation(
            parentColumn = "id",
            entityColumn = "playlist_id",
            entity = PlaylistSongEntity::class
        )
        val songs: List<SongWithPlaylistSong>
    )

    data class SongWithPlaylistSong(
        @Embedded
        val playlistSongEntity: PlaylistSongEntity,
        @Relation(
            parentColumn = "song_id",
            entityColumn = "id"
        )
        val songEntity: SongEntity
    )
}