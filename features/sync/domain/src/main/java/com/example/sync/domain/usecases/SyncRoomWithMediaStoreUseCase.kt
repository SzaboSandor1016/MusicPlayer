package com.example.sync.domain.usecases

import com.example.core.common.values.RECENT_ID
import com.example.features.playlists.domain.models.PlaylistSongPlaylistsDomainModel
import com.example.features.playlists.domain.repository.PlaylistsRepository
import com.example.features.songs.domain.repository.SongsRepository
import kotlinx.coroutines.flow.first
import java.time.Instant

class SyncRoomWithMediaStoreUseCase(
    private val songsRepository: SongsRepository,
    private val playlistsRepository: PlaylistsRepository
) {

    val THIRTY_DAYS_IN_SECONDS = 2592000L

    suspend operator fun invoke() {

        val mediaStoreItems = songsRepository.getAllSongsFromMediaStore().first()

        val roomItems = songsRepository.getAllSongsFromRoom().first()

        val associations = playlistsRepository.getPlaylistSongsByPlaylistId(RECENT_ID).first().map { it.songId }

        val mediaStoreIds = mediaStoreItems.associateBy { it.id }
        val roomIds = roomItems.associateBy { it.id }

        val addedItems = mediaStoreItems.filter { it.id !in roomIds }

        val removedItems = roomItems.filter { it.id !in mediaStoreIds }

        val updated = roomItems.mapNotNull {

            val item = mediaStoreIds[it.id]?: return@mapNotNull null

            if (it != item) item else null
        }

        val thresholdDate = Instant.now().minusSeconds(THIRTY_DAYS_IN_SECONDS).toEpochMilli()

        val recents = mediaStoreItems.filter { it.dateAdded >= thresholdDate }.mapIndexed { index, model ->
             PlaylistSongPlaylistsDomainModel(RECENT_ID,model.id, index + 1)
        }.filter { it.songId !in associations }

        songsRepository.insertSongs(addedItems)

        songsRepository.deleteSongs(removedItems)

        songsRepository.updateSongs(updated)

        playlistsRepository.insertPlaylistSongs(recents)
    }
}