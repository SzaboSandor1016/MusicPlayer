package com.example.datasources.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datasources.database.dao.ArtistDao
import com.example.datasources.database.dao.SongDao
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import kotlin.test.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class SongDaoTest {

    private lateinit var testDatabase: AppDatabase

    private lateinit var testSongDao: SongDao

    private lateinit var testArtistDao: ArtistDao

    @Before
    fun setUp() {

        testDatabase = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = AppDatabase::class.java
        ).allowMainThreadQueries().build()

        testSongDao = testDatabase.songDao()

        testArtistDao = testDatabase.artistDao()
    }

    @After
    fun destroy() {

        testDatabase.close()
    }

    @Test
    fun insert_song_and_verify_by_getting_all_Test() = runTest {

        val testArtist = ArtistEntity(
            id = 0L,
            name = "testArtist"
        )

        testArtistDao.insertArtists(listOf(testArtist))

        val testSong = SongEntity(
            displayName = "test",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )

        testSongDao.insertSongs(listOf(testSong))

        val inserted = testSongDao.getAllSongsWithArtists().first().first()

        assertTrue( inserted.song.msId == testSong.msId && inserted.artist == testArtist.name)
    }

    @Test
    fun insert_existing_song_throws_error_test() = runTest {

        val existing = SongEntity(
            displayName = "test",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )

        testSongDao.insertSongs(listOf(existing))

        val duplicate = SongEntity(
            id = 1L,
            displayName = "test",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )

        assertFails { testSongDao.insertSongs(listOf(duplicate)) }
    }

    @Test
    fun delete_song_and_verify_by_getting_empty_list() = runTest {

        val testArtist = ArtistEntity(
            id = 0L,
            name = "testArtist"
        )

        testArtistDao.insertArtists(listOf(testArtist))

        val testSong = SongEntity(
            id = 1L,
            displayName = "test",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )

        testSongDao.insertSongs(listOf(testSong))

        val inserted = testSongDao.getAllSongsWithArtists().first().first()

        testSongDao.deleteSongs(listOf(inserted.song))

        val afterDelete = testSongDao.getAllSongsWithArtists().first()

        assertTrue(afterDelete.isEmpty())
    }

    @Test
    fun update_song_verify_updated() = runTest {

        val testArtist = ArtistEntity(
            id = 0L,
            name = "testArtist"
        )

        testArtistDao.insertArtists(listOf(testArtist))

        val testSong = SongEntity(
            id = 1L,
            displayName = "test",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )

        testSongDao.insertSongs(listOf(testSong))

        val updated = SongEntity(
            id = 1L,
            displayName = "updated",
            duration = 2,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )

        testSongDao.updateSongs(listOf(updated))

        val afterUpdate = testSongDao.getAllSongsWithArtists().first().first()

        assertTrue { afterUpdate.song.id == updated.id && afterUpdate.artist == testArtist.name }
    }

    @Test
    fun getAllSongsWithArtistsTest() = runTest {

        val testArtist = ArtistEntity(
            id = 0L,
            name = "testArtist"
        )

        testArtistDao.insertArtists(listOf(testArtist))

        val testSongs = listOf(
            SongEntity(
                displayName = "test1",
                duration = 2,
                albumId = 0L,
                artistId = 0L,
                genreId = 0L,
                dateAdded = 0L,
                msId = 1L,
            ),
            SongEntity(
                displayName = "test2",
                duration = 3,
                albumId = 0L,
                artistId = 0L,
                genreId = 0L,
                dateAdded = 0L,
                msId = 2L,
            )
        )

        testSongDao.insertSongs(testSongs)

        val inserted = testSongDao.getAllSongsWithArtists().first()

        assertTrue {

            inserted.size == 2
        }
    }
}