package com.example.datasources.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datasources.database.dao.SongDao
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

    @Before
    fun setUp() {

        testDatabase = Room.inMemoryDatabaseBuilder(
            context = ApplicationProvider.getApplicationContext(),
            klass = AppDatabase::class.java
        ).allowMainThreadQueries().build()

        testSongDao = testDatabase.songDao()
    }

    @After
    fun destroy() {

        testDatabase.close()
    }

    @Test
    fun insert_song_and_verify_by_getting_all_Test() = runTest {

        val testSong = SongEntity(
            id = 1L,
            displayName = "test",
            duration = 3,
            author = "testAuthor",
            album = 0L,
            dateAdded = 0L,
        )

        testSongDao.insertSongs(listOf(testSong))

        val inserted = testSongDao.getAllSongs().first().first()

        assertTrue(inserted == testSong)
    }

    @Test
    fun insert_existing_song_throws_error_test() = runTest {

        val existing = SongEntity(
            id = 1L,
            displayName = "test",
            duration = 3,
            author = "testAuthor",
            album = 0L,
            dateAdded = 0L,
        )

        testSongDao.insertSongs(listOf(existing))

        val duplicate = SongEntity(
            id = 1L,
            displayName = "test",
            duration = 3,
            author = "testAuthor",
            album = 0L,
            dateAdded = 0L,
        )

        assertFails { testSongDao.insertSongs(listOf(duplicate)) }
    }

    @Test
    fun delete_song_and_verify_by_getting_empty_list() = runTest {

        val testSong = SongEntity(
            id = 1L,
            displayName = "test",
            duration = 3,
            author = "testAuthor",
            album = 0L,
            dateAdded = 0L,
        )

        testSongDao.insertSongs(listOf(testSong))

        val inserted = testSongDao.getAllSongs().first().first()

        testSongDao.deleteSongs(listOf(inserted))

        val afterDelete = testSongDao.getAllSongs().first()

        assertTrue(afterDelete.isEmpty())
    }

    @Test
    fun update_song_verify_updated() = runTest {

        val testSong = SongEntity(
            id = 1L,
            displayName = "test",
            duration = 3,
            author = "testAuthor",
            album = 0L,
            dateAdded = 0L,
        )

        testSongDao.insertSongs(listOf(testSong))

        val updated = SongEntity(
            id = 1L,
            displayName = "updated",
            duration = 2,
            author = "updatedAuthor",
            album = 0L,
            dateAdded = 0L,
        )

        testSongDao.updateSongs(listOf(updated))

        val afterUpdate = testSongDao.getAllSongs().first().first()

        assertTrue { afterUpdate == updated }
    }

    @Test
    fun getAllSongsTest() = runTest {

        val testSongs = listOf(
            SongEntity(
                id = 1L,
                displayName = "test1",
                duration = 2,
                author = "testAuthor1",
                album = 0L,
                dateAdded = 0L,
            ),
            SongEntity(
                id = 2L,
                displayName = "test2",
                duration = 3,
                author = "testAuthor2",
                album = 0L,
                dateAdded = 0L,
            )
        )

        testSongDao.insertSongs(testSongs)

        val inserted = testSongDao.getAllSongs().first()

        assertTrue {

            inserted.first() == testSongs.first() &&
                    inserted.last() == testSongs.last()
        }
    }
}