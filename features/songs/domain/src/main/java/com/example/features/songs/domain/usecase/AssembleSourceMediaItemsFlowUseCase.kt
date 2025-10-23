package com.example.features.songs.domain.usecase

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import androidx.core.net.toUri

class AssembleSourceMediaItemsFlowUseCase(
    private val getSongMetadataByIdFlowUseCase: GetSongMetadataByIdFlowUseCase,
) {

    suspend operator fun invoke(songIds: List<Long>): List<MediaItem> {

        return withContext(Dispatchers.IO){
            return@withContext songIds.mapNotNull { id ->
                val metadata = getSongMetadataByIdFlowUseCase(id).first()
                    ?: return@mapNotNull null

                val contentUri: Uri =
                    ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                val sArt = "content://media/external/audio/albumart".toUri()
                val artworkUri: Uri =
                    ContentUris.withAppendedId(
                        sArt,
                        metadata.albumId
                    )

                val mediaMetadata = MediaMetadata.Builder()
                    .setTitle(metadata.displayName)
                    .setDurationMs(metadata.duration.toLong())
                    .setArtist(metadata.author)
                    .setArtworkUri(artworkUri)
                    .build()

                MediaItem.Builder()
                    .setUri(contentUri)
                    .setMediaId(id.toString())
                    .setMediaMetadata(
                        mediaMetadata
                    ).build()
            }
        }
    }
}