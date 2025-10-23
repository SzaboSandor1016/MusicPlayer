package com.example.datasources.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datasources.database.dao.ArtistDao
import com.example.datasources.database.dao.PlaylistsDao
import com.example.datasources.database.dao.SongDao
import com.example.datasources.database.dao.entities.ArtistEntity
import com.example.datasources.database.dao.entities.PlaylistEntity
import com.example.datasources.database.dao.entities.PlaylistSongEntity
import com.example.datasources.database.dao.entities.SongEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class PlaylistDaoTest {

    private lateinit var testDatabase: AppDatabase

    private lateinit var testPlaylistsDao: PlaylistsDao

    private lateinit var testSongDao: SongDao

    private lateinit var testArtistDao: ArtistDao

    @Before
    fun setUp() {

        testDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        testPlaylistsDao = testDatabase.playlistsDao()

        testSongDao = testDatabase.songDao()

        testArtistDao = testDatabase.artistDao()
    }

    @After
    fun destroy() {

        testDatabase.close()
    }

    @Test
    fun insertPlaylistTest() = runTest {

        val testPlaylist = PlaylistEntity(
            id = 0,
            label = "testPlaylist",
            type = 2
        )

        testPlaylistsDao.insertNewPlaylist(
            playlistEntity = testPlaylist
        )

        val inserted = testPlaylistsDao.getAllPlaylists().first()

        assertTrue {

            inserted.first().id == 1L &&
                    inserted.first().label == "testPlaylist"

        }
    }

    @Test
    fun deletePlaylistTest() = runTest {

        val testPlaylist = PlaylistEntity(
            id = 0,
            label = "testPlaylist",
            type = 2
        )

        testPlaylistsDao.insertNewPlaylist(
            playlistEntity = testPlaylist
        )

        val inserted = testPlaylistsDao.getAllPlaylists().first()

        testPlaylistsDao.deletePlaylist(inserted.first().id)

        val afterDelete = testPlaylistsDao.getAllPlaylists().first()

        assertTrue {

            afterDelete.isEmpty()
        }
    }

    @Test
    fun insertPlaylistSongTest() = runTest {

        val testPlaylist = PlaylistEntity(
            id = 0,
            label = "testPlaylist",
            type = 2
        )

        val testPlaylistSong = PlaylistSongEntity(
            playlistId = 1L,
            songId = 1,
            order = 1
        )
        val testSong = SongEntity(
            displayName = "testSong",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 1000L,
            msId = 1L,
        )

        val testArtist = ArtistEntity(
            id = 0L,
            name = "testArtist"
        )

        testPlaylistsDao.insertNewPlaylist(
            playlistEntity = testPlaylist
        )

        testArtistDao.insertArtists(listOf(testArtist))

        testSongDao.insertSongs(listOf(testSong))

        testPlaylistsDao.insertNewPlaylistSong(
            testPlaylistSong
        )

        val inserted = testPlaylistsDao.getAllPlaylists().first()

        assertTrue {

            inserted.first().id == 1L &&
                    inserted.first().label == "testPlaylist"
        }
    }

    @Test
    fun deletePlaylistSongTest() = runTest {

        val testPlaylist = PlaylistEntity(
            id = 0,
            label = "testPlaylist",
            type = 2
        )

        val testPlaylistSong = PlaylistSongEntity(
            playlistId = 1L,
            songId = 1,
            order = 1
        )
        val testSong = SongEntity(
            displayName = "testSong",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 1000L,
            msId = 1L,
        )

        testPlaylistsDao.insertNewPlaylist(
            playlistEntity = testPlaylist
        )

        testSongDao.insertSongs(listOf(testSong))

        testPlaylistsDao.insertNewPlaylistSong(
            testPlaylistSong
        )

        val inserted = testPlaylistsDao.getAllAssociations().first()

        testPlaylistsDao.deletePlaylistSong(
            playlistId = inserted.first().playlistId,
            songId = inserted.first().songId
        )

        val afterDelete = testPlaylistsDao.getAllAssociations().first()

        assertTrue {

            afterDelete.isEmpty()
        }
    }

    @Test
    fun deletePlaylistSongAfterAddingMultipleSongsTest() = runTest {

        val testPlaylist = PlaylistEntity(
            id = 0,
            label = "testPlaylist",
            type = 2,
        )

        val testPlaylistSong = PlaylistSongEntity(
            playlistId = 1L,
            songId = 1,
            order = 1
        )
        val testPlaylistSong1 = PlaylistSongEntity(
            playlistId = 1L,
            songId = 2,
            order = 2
        )

        val testSong = SongEntity(
            displayName = "testSong",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )

        val testSong1 = SongEntity(
            displayName = "testSong1",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 2L,
        )

        testPlaylistsDao.insertNewPlaylist(
            playlistEntity = testPlaylist
        )

        testSongDao.insertSongs(listOf(testSong, testSong1))

        testPlaylistsDao.insertNewPlaylistSong(
            testPlaylistSong
        )

        testPlaylistsDao.insertNewPlaylistSong(
            testPlaylistSong1
        )

        val inserted = testPlaylistsDao.getAllAssociations().first()

        testPlaylistsDao.deletePlaylistSong(
            playlistId = testPlaylistSong.playlistId,
            songId = testPlaylistSong.songId
        )

        val afterDelete = testPlaylistsDao.getAllAssociations().first()

        assertTrue {

            afterDelete.isNotEmpty() && afterDelete.first().playlistId == 1L
                    && afterDelete.first().songId == 2L
        }
    }

    @Test
    fun deletePlaylistSongAfterAddingMultiplePlaylistsTest() = runTest {

        val testPlaylist = PlaylistEntity(
            id = 0,
            label = "testPlaylist",
            type = 2,
        )
        val testPlaylist1 = PlaylistEntity(
            id = 0,
            label = "testPlaylist1",
            type = 2
        )

        val testPlaylistSong = PlaylistSongEntity(
            playlistId = 1L,
            songId = 1,
            order = 1
        )
        val testPlaylistSong1 = PlaylistSongEntity(
            playlistId = 2L,
            songId = 1,
            order = 1
        )
        val testSong = SongEntity(
            displayName = "testSong",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )

        testPlaylistsDao.insertNewPlaylist(
            playlistEntity = testPlaylist
        )
        testPlaylistsDao.insertNewPlaylist(
            playlistEntity = testPlaylist1
        )

        testSongDao.insertSongs(listOf(testSong))

        testPlaylistsDao.insertNewPlaylistSong(
            testPlaylistSong
        )

        testPlaylistsDao.insertNewPlaylistSong(
            testPlaylistSong1
        )

        testPlaylistsDao.deletePlaylistSong(
            playlistId = testPlaylistSong.playlistId,
            songId = testPlaylistSong.songId
        )

        val afterDelete = testPlaylistsDao.getAllAssociations().first()

        assertTrue {

            afterDelete.isNotEmpty() && afterDelete.first().playlistId == 2L
                    && afterDelete.first().songId == 1L
        }
    }

    @Test
    fun updatePlaylistSongTest() = runTest {

        val testPlaylist = PlaylistEntity(
            id = 0,
            label = "testPlaylist",
            type = 2,
        )

        val testPlaylistSong = PlaylistSongEntity(
            playlistId = 1L,
            songId = 1,
            order = 1
        )
        val testPlaylistSong1 = PlaylistSongEntity(
            playlistId = 1L,
            songId = 2,
            order = 2
        )
        val testSong = SongEntity(
            displayName = "testSong",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 1L,
        )
        val testSong1 = SongEntity(
            displayName = "testSong",
            duration = 3,
            albumId = 0L,
            artistId = 0L,
            genreId = 0L,
            dateAdded = 0L,
            msId = 2L,
        )

        testPlaylistsDao.insertNewPlaylist(
            playlistEntity = testPlaylist
        )

        testSongDao.insertSongs(listOf(testSong,testSong1))

        testPlaylistsDao.insertNewPlaylistSong(
            testPlaylistSong
        )

        testPlaylistsDao.insertNewPlaylistSong(
            testPlaylistSong1
        )

        val inserted = testPlaylistsDao.getAllAssociations().first()

        assertTrue {

            inserted.first().order == 1 && inserted.last().order == 2
        }

        testPlaylistsDao.updatePlaylistSongs(
            listOf(
                testPlaylistSong.copy(
                    order = 2
                ),
                testPlaylistSong1.copy(
                    order = 1
                )
            )
        )

        val afterDelete = testPlaylistsDao.getAllAssociations().first()

        assertTrue {

            afterDelete.first().order == 2 && afterDelete.last().order == 1
        }
    }
}