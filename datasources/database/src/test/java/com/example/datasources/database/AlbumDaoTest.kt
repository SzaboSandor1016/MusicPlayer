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
class AlbumDaoTest {

    private lateinit var testDatabase: AppDatabase

    private lateinit var testAlbumDao: AlbumDao

    private lateinit var testArtistDao: ArtistDao

    private lateinit var testSongDao: SongDao

    @Before
    fun setUp() {

        testDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        testAlbumDao = testDatabase.albumDao()
        testArtistDao = testDatabase.artistDao()
        testSongDao = testDatabase.songDao()
    }

    suspend fun insertTestPrerequisites() {

        val testArtist = ArtistEntity(
            id = 0L,
            name = "0L",
        )

        testArtistDao.insertArtists(listOf(testArtist))

        val testSongs = listOf(
            SongEntity(
                displayName = "0L",
                duration = 0,
                albumId = 0L,
                artistId = 0L,
                genreId = 0L,
                dateAdded = 1000,
                msId = 0L,
            ),
            SongEntity(
                displayName = "1L",
                duration = 1,
                albumId = 1L,
                artistId = 0L,
                genreId = 1L,
                dateAdded = 1000,
                msId = 1L,
            ),
        )

        testSongDao.insertSongs(testSongs)
    }

    @After
    fun destroy() {

        testDatabase.close()
    }

    @Test
    fun insertAlbumsTest() = runTest {

        val testAlbums = listOf(
            AlbumEntity(
                id = 0L,
                name = "0L",
            ),
            AlbumEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testAlbumDao.insertAlbums(testAlbums)

        val inserted = testAlbumDao.getAllAlbums().first()

        assertTrue {
            inserted.size == 2 && inserted.firstOrNull { it.id == 0L } != null
                    && inserted.firstOrNull { it.id == 1L } != null
        }
    }

    @Test
    fun deleteAlbumsTest() = runTest {

        val testAlbums = listOf(
            AlbumEntity(
                id = 0L,
                name = "0L",
            ),
            AlbumEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testAlbumDao.insertAlbums(testAlbums)

        val inserted = testAlbumDao.getAllAlbums().first()

        assertTrue {
            inserted.size == 2
        }

        assertTrue {
            inserted.firstOrNull { it.id == 0L } != null
        }

        assertTrue {
            inserted.firstOrNull { it.id == 1L } != null
        }

        testAlbumDao.deleteAlbums(inserted)

        val deleted = testAlbumDao.getAllAlbums().first()

        assertTrue {
            deleted.isEmpty()
        }

        assertTrue {
            deleted.firstOrNull { it.id == 0L } == null
        }

        assertTrue {
            deleted.firstOrNull { it.id == 1L } == null
        }
    }

    /*@Test
    fun getAllAlbumsWithSongsTest() = runTest {

        insertTestPrerequisites()

        val testAlbums = listOf(
            AlbumEntity(
                id = 0L,
                name = "0L",
            ),
            AlbumEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testAlbumDao.insertAlbums(testAlbums)

        val inserted = testAlbumDao.getAllAlbums().first()

        val first = inserted.find { it.id == testAlbums[0].id }

        val second = inserted.find { it.id == testAlbums[1].id }

        assertTrue {
            inserted.isNotEmpty()
        }
        assertTrue {
            first != null && first.songs.isNotEmpty()
        }
        assertTrue {
            second != null && second.songs.isNotEmpty()
        }
    }*/
}