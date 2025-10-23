package com.example.datasources.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datasources.database.dao.ArtistDao
import com.example.datasources.database.dao.GenreDao
import com.example.datasources.database.dao.SongDao
import com.example.datasources.database.dao.entities.AlbumEntity
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.GenreEntity
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class GenreDaoTest {

    private lateinit var testDatabase: AppDatabase

    private lateinit var testGenreDao: GenreDao

    private lateinit var testSongDao: SongDao

    private lateinit var testArtistDao: ArtistDao

    @Before
    fun setUp() {

        testDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        testGenreDao = testDatabase.genreDao()
        testSongDao = testDatabase.songDao()
        testArtistDao = testDatabase.artistDao()
    }

    @After
    fun destroy() {

        testDatabase.close()
    }

    suspend fun insertTestPrerequisites() {

        val testArtist = ArtistEntity(
            id = 0L,
            name = "0L"
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

    @Test
    fun insertAlbumsTest() = runTest {

        val testGenres = listOf(
            GenreEntity(
                id = 0L,
                name = "0L",
            ),
            GenreEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testGenreDao.insertGenres(testGenres)

        val inserted = testGenreDao.getAllGenres().first()

        assertTrue {
            inserted.size == 2 && inserted.firstOrNull { it.id == 0L } != null
                    && inserted.firstOrNull { it.id == 1L } != null
        }
    }

    @Test
    fun deleteAlbumsTest() = runTest {

        val testGenres = listOf(
            GenreEntity(
                id = 0L,
                name = "0L",
            ),
            GenreEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testGenreDao.insertGenres(testGenres)

        val inserted = testGenreDao.getAllGenres().first()

        assertTrue {
            inserted.size == 2 && inserted.firstOrNull { it.id == 0L } != null
                    && inserted.firstOrNull { it.id == 1L } != null
        }

        testGenreDao.deleteGenres(inserted)

        val deleted = testGenreDao.getAllGenres().first()

        assertTrue {
            deleted.isEmpty() && deleted.firstOrNull { it.id == 0L } == null
                    && deleted.firstOrNull { it.id == 1L } == null
        }
    }

    /*@Test
    fun getAllGenresWithSongsTest() = runTest {

        insertTestPrerequisites()

        val testGenres = listOf(
            GenreEntity(
                id = 0L,
                name = "0L",
            ),
            GenreEntity(
                id = 1L,
                name = "1L",
            ),
        )

        testGenreDao.insertGenres(testGenres)

        val inserted = testGenreDao.getAllGenresWithSongs().first()

        val first = inserted.find { it.genre.id == testGenres[0].id }

        val second = inserted.find { it.genre.id == testGenres[1].id }

        assertTrue {
            inserted.isNotEmpty()
                    && first != null && first.songs.isNotEmpty()
                    && second != null && second.songs.isNotEmpty()
        }
    }*/
}