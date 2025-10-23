package com.example.datasources.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datasources.database.dao.AlbumDao
import com.example.datasources.database.dao.ArtistDao
import com.example.datasources.database.dao.SongDao
import com.example.datasources.database.dao.entities.AlbumEntity
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ArtistDaoTest {

    private lateinit var testDatabase: AppDatabase

    private lateinit var testArtistDao: ArtistDao

    private lateinit var testAlbumDao: AlbumDao

    private lateinit var testSongDao: SongDao

    @Before
    fun setUp() {

        testDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        testArtistDao = testDatabase.artistDao()
        testAlbumDao = testDatabase.albumDao()
        testSongDao = testDatabase.songDao()
    }

    suspend fun insertTestPrerequisites() {

        val testAlbum = AlbumEntity(
            id = 0L,
            name = "0L",
        )

        testAlbumDao.insertAlbums(listOf(testAlbum))

        val testSongs = listOf(
            SongEntity(
                displayName = "0L",
                duration = 0,
                albumId = 0L,
                artistId = 0L,
                genreId = 0L,
                dateAdded = 1000,
                msId = 0L,
                key = "test/tes1|10_10_.mp3"
            ),
            SongEntity(
                displayName = "1L",
                duration = 1,
                albumId = 0L,
                artistId = 0L,
                genreId = 1L,
                dateAdded = 1000,
                msId = 1L,
                key = "test/tes1|10_10_.mp3"
            ),
        )

        testSongDao.insertSongs(testSongs)
    }

    @After
    fun destroy() {

        testDatabase.close()
    }

    @Test
    fun insertArtistsTest() = runTest {

        val testArtists = listOf(
            ArtistEntity(
                id = 0L,
                name = "0L",
            ),
            ArtistEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testArtistDao.insertArtists(testArtists)

        val inserted = testArtistDao.getAllArtists().first()

        assertTrue {
            inserted.size == 2 && inserted.firstOrNull { it.id == 0L } != null
                    && inserted.firstOrNull { it.id == 1L } != null
        }
    }

    @Test
    fun deleteArtistsTest() = runTest {

        val testArtists = listOf(
            ArtistEntity(
                id = 0L,
                name = "0L",
            ),
            ArtistEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testArtistDao.insertArtists(testArtists)

        val inserted = testArtistDao.getAllArtists().first()

        assertTrue {
            inserted.size == 2 && inserted.firstOrNull { it.id == 0L } != null
                    && inserted.firstOrNull { it.id == 1L } != null
        }

        testArtistDao.deleteArtists(inserted)

        val deleted = testArtistDao.getAllArtists().first()

        assertTrue {
            deleted.isEmpty() && deleted.firstOrNull { it.id == 0L } == null
                    && deleted.firstOrNull { it.id == 1L } == null
        }
    }

    /*@Test
    fun getAllArtistsWithAlbumsSongsTest() = runTest {

        insertTestPrerequisites()

        val testArtists = listOf(
            ArtistEntity(
                id = 0L,
                name = "0L",
            ),
            ArtistEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testArtistDao.insertArtists(testArtists)

        val inserted = testArtistDao.getAllArtistsWithSongs().first()

        val first = inserted.find { it.artist.id == testArtists[0].id }

        val second = inserted.find { it.artist.id == testArtists[1].id }

        assertTrue {
            inserted.isNotEmpty()
                    && first != null && first.albumsWithSongs.isNotEmpty()
                    && second != null && second.albumsWithSongs.isEmpty()
        }
    }*/
}