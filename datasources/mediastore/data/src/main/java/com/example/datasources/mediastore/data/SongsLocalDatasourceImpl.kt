package com.example.datasources.mediastore.data

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.example.datasources.mediastore.domain.SongsLocalDatasource
import com.example.datasources.mediastore.domain.models.SongMetadataSongsLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.SongSongsLocalDatasourceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import androidx.core.net.toUri

class SongsLocalDatasourceImpl(
    private val context: Context
): SongsLocalDatasource {

    override fun getAllSongsOnDevice(): Flow<List<SongSongsLocalDatasourceModel>> {

        val songs = mutableListOf<SongSongsLocalDatasourceModel>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATE_ADDED
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf("30000") // min 30 seconds, adjust as needed

        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
        query?.use { cursor ->
            // Cache column indices.
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST)

            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(titleColumn) ?: cursor.getString(nameColumn)
                    ?.substringBeforeLast(".") ?: ""
                val duration = cursor.getInt(durationColumn)
                val artist = cursor.getString(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val dateAdded = cursor.getLong(dateAddedColumn) * 1000L

                val artistString = getArtistString(artist)

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                songs += SongSongsLocalDatasourceModel(
                    id =  id,
                    displayName = name,
                    duration =  duration,
                    author = artistString,
                    albumId = albumId,
                    dateAdded = dateAdded
                )
            }
        }

        return flowOf(songs.toList())
    }

    override fun getMediaItemFromId(id: Long): SongMetadataSongsLocalDatasourceModel? {

        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )

        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
        )

        context.contentResolver.query(
            contentUri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
                val albumId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))

                val artistString = getArtistString(artist)

                return SongMetadataSongsLocalDatasourceModel(
                    displayName = title,
                    duration = duration,
                    author = artistString,
                    albumId = albumId
                )
            }
        }
        return null
    }

    private fun getArtistString(raw: String): String {

        return if (raw == "<unknown>") context.getString(com.example.core.ui.R.string.unknown_artist) else raw
    }

    /*override fun getMediaItemAlbumFromId(uri: Uri): Bitmap? {

        return try {
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            null
        }

        *//*return ContentUris.withAppendedId(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            id
        )*//*
    }

    override fun getEmbeddedAlbumArt(uri: Uri): Bitmap? {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            retriever.embeddedPicture?.let { data ->
                BitmapFactory.decodeByteArray(data, 0, data.size)
            }
        } catch (e: Exception) {
            null
        } finally {
            retriever.release()
        }
    }*/
}