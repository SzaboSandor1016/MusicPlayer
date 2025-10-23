package com.example.sync.domain.usecases

import com.example.core.common.values.RECENT_ID
import com.example.features.albums.domain.repository.AlbumsRepository
import com.example.features.artists.domain.repository.ArtistsRepository
import com.example.features.genres.domain.repository.GenresRepository
import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository
import com.example.features.songs.domain.model.SongSongsDomainModel
import com.example.features.songs.domain.repository.SongsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.Instant
import kotlin.math.max
import kotlin.math.min

class SyncRoomWithMediaStoreUseCase(
    private val songsRepository: SongsRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val albumsRepository: AlbumsRepository,
    private val artistsRepository: ArtistsRepository,
    private val genresRepository: GenresRepository
) {

    val THIRTY_DAYS_IN_SECONDS = 2592000L

    suspend operator fun invoke() {

        withContext(Dispatchers.IO) {

            syncGenres()

            syncAlbums()

            syncArtists()

            val mediaStoreItems = songsRepository.getAllSongsFromMediaStore().first()

            syncSongs(
                mediaStoreItems = mediaStoreItems
            )

            syncRecents()
        }
    }

    private suspend fun syncSongs(mediaStoreItems: List<SongSongsDomainModel.Entity>) {

        val roomItems = songsRepository.getAllSongsEntityFromRoom().first()

        val mediaStoreIds = mediaStoreItems.associateBy { it.key }

        val mediaStorePrimaryKeys = mediaStoreItems.groupBy { it.key.substringAfter("|") }

        //val mediaStoreAuxKeys = mediaStoreItems.groupBy { it.key.substringBefore("|") }

        val roomIds = roomItems.associateBy { it.key }

        val roomKeys = roomItems.groupBy { it.key.substringAfter("|") }

        val addedItems = mutableListOf<SongSongsDomainModel.Entity>()

        val updatedItems = mutableListOf<SongSongsDomainModel.Entity>()

        mediaStoreIds.forEach { mediaItem ->

            mediaStorePrimaryKeys[mediaItem.key.substringAfter("|")]?.let { mediaStoreItems ->

                /*val mediaStoreSecondaryKeys = mediaStoreItems.associateBy { it.key.substringBefore("|") }

                roomKeys[mediaItem.key.substringAfter("|")]?.let { existing ->


                }?: addedItems.add(mediaItem.value)*/

                //TODO handle relativePath change
                mediaStoreItems.forEach { item ->

                    roomKeys[item.key.substringAfter("|")]?.let { existingGroup ->

                        if (existingGroup.size == 1) {

                            updatedItems.add(
                                item.copy(
                                    id = existingGroup.first().id
                                )
                            )
                        } else {

                            existingGroup.find {
                                it.key.substringBefore("|") == item.key.substringBefore("|")
                            }?.let {

                                val updated = item.copy(
                                    id = it.id
                                )

                                if (!updatedItems.contains(updated)) {

                                    updatedItems.add(
                                        updated
                                    )
                                }
                            }?: { if (!addedItems.contains(item)) addedItems.add(item) }
                        }
                    }?: if (!addedItems.contains(item)) addedItems.add(item) else {}
                }
            }
        }

        val updatedIds = updatedItems.map { it.id }

        val removedItems = roomItems.filter {

            it.id !in updatedIds && mediaStoreIds[it.key] == null
        }

        songsRepository.updateSongs(updatedItems)

        songsRepository.insertSongs(addedItems)

        songsRepository.deleteSongs(removedItems)
    }

    private suspend fun syncRecents() {

        val associations = playlistsRepository.getPlaylistSongsByPlaylistId(RECENT_ID).first()

        val roomItems = songsRepository.getAllSongsEntityFromRoom().first()

        val thresholdDate = Instant.now().minusSeconds(THIRTY_DAYS_IN_SECONDS).toEpochMilli()

        val notRecents = roomItems.filter {
            it.dateAdded < thresholdDate
        }.map { it.id }

        val deleted = associations.filter {
            it.songId in notRecents
        }

        val remaining = associations.filter {

            it.songId !in notRecents
        }.mapIndexed { index, model ->
            model.copy(
                order = index + 1
            )
        }

        val remainingIds = remaining.map { it.songId }

        val recents = roomItems.filter {

            it.dateAdded >= thresholdDate && it.id !in remainingIds
        }.mapIndexed { index, model ->

            PlaylistSongPlaylistsDomainModel(RECENT_ID,model.id, remaining.size + index + 1)
        }

        playlistsRepository.updatePlaylistSongs(remaining)

        playlistsRepository.insertPlaylistSongs(recents)

        playlistsRepository.deletePlaylistSongs(deleted)
    }

    private suspend fun syncGenres() {

        val mediaStoreGenres = genresRepository.getAllGenresFromMediaStore().first()

        val roomGenres = genresRepository.getAllGenresFromRoom().first()

        val mediaStoreIds = mediaStoreGenres.associateBy { it.id }
        val roomIds = roomGenres.associateBy { it.id }

        val addedItems = mediaStoreGenres.filter { genre ->

            genre.id !in roomIds || roomIds[genre.id]?.name?.let {
                genre.name != it
            }?: false
        }

        val removedItems = roomGenres.filter { it.id !in mediaStoreIds }

        genresRepository.insertGenres(addedItems)

        genresRepository.deleteGenres(removedItems)
    }

    private suspend fun syncAlbums() {

        val mediaStoreAlbums = albumsRepository.getAllAlbumsFromMediaStore().first()

        val roomAlbums = albumsRepository.getAllAlbumsFromRoom().first()

        val mediaStoreIds = mediaStoreAlbums.associateBy { it.id }
        val roomIds = roomAlbums.associateBy { it.id }

        val addedItems = mediaStoreAlbums.filter { album ->

            album.id !in roomIds || roomIds[album.id]?.let {
                it.name != album.name
            }?: false
        }

        val removedItems = roomAlbums.filter { it.id !in mediaStoreIds }

        albumsRepository.insertAlbums(addedItems)

        albumsRepository.deleteAlbums(removedItems)
    }

    private suspend fun syncArtists() {

        val mediaStoreArtists = artistsRepository.getAllArtistsFromMediaStore().first()

        val roomArtists = artistsRepository.getAllArtistsFromRoom().first()

        val mediaStoreIds = mediaStoreArtists.associateBy { it.id }
        val roomIds = roomArtists.associateBy { it.id }

        val addedItems = mediaStoreArtists.filter { artist ->

            artist.id !in roomIds || roomIds[artist.id]?.name?.let {
                artist.name != it
            }?: false
        }

        val removedItems = roomArtists.filter { it.id !in mediaStoreIds }

        artistsRepository.insertArtists(addedItems)

        artistsRepository.deleteArtists(removedItems)
    }

    private fun String.similarTo(other: String): Double =
        1.0 - levenshteinDistance(this.lowercase(), other.lowercase()) / max(length, other.length).toDouble()

    private fun levenshteinDistance(string: String, string1: String): Int {

        val m = string.length
        val n = string1.length

        if (m == 0) return n
        if (n == 0) return m

        var prevRow = MutableList(n + 1) { it }
        var currentRow = MutableList(n + 1) { 0 }

        for (i in 1..m) {

            currentRow[0] = i

            for(j in 1..n) {

                val deletionCost = prevRow[j] + 1

                val insertionCost = currentRow[j - 1] + 1

                val substitutionCost = if (string[i-1] == string1[j-1]) {
                    prevRow[j - 1]
                } else {
                    prevRow[j - 1] + 1
                }

                currentRow[j] = min(
                    deletionCost,
                    min(
                        insertionCost,
                        substitutionCost
                    )
                )
            }

            val temp = prevRow
            prevRow = currentRow
            currentRow = temp
        }

        return prevRow[n]
    }
}