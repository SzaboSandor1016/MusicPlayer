package com.example.datasources.mediastore.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.core.common.values.DEFAULT_ALBUM_NAME
import com.example.core.common.values.DEFAULT_ARTIST_NAME
import com.example.core.common.values.DEFAULT_GENRE_NAME
import com.example.core.common.values.DEFAULT_SONG_TITLE
import com.example.datasources.mediastore.domain.MediaStoreLocalDatasource
import com.example.datasources.mediastore.domain.models.SongMetadataMediaStoreLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.SongMediaStoreLocalDatasourceModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.example.core.ui.R
import com.example.datasources.mediastore.domain.models.AlbumMediaStoreLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.ArtistMediaStoreLocalDatasourceModel
import com.example.datasources.mediastore.domain.models.GenreMediaStoreLocalDatasourceModel

class MediaStoreLocalDatasourceImpl(
    private val context: Context
): MediaStoreLocalDatasource {

    override fun getAllSongsOnDevice(): Flow<List<SongMediaStoreLocalDatasourceModel>> {

        val songs = mutableListOf<SongMediaStoreLocalDatasourceModel>()

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.GENRE_ID,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE,
            MediaStore.Audio.Media.RELATIVE_PATH
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
            //TODO rewrite with getColumnIndex and -1 checks
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID)
            val nameColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            val titleColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val durationColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val artistColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID)
            val albumIdColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)
            val genreColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.GENRE_ID)
            val sizeColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)
            val mimeColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE)
            val relativePathColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH)

            val dateAddedColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)

                val title = cursor.getString(titleColumn) ?: cursor.getString(nameColumn)
                            ?.substringBeforeLast(".") ?: DEFAULT_SONG_TITLE

                val duration = cursor.getInt(durationColumn)
                val artistId = cursor.getLong(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val genreId = cursor.getLong(genreColumn)
                val dateAdded = cursor.getLong(dateAddedColumn) * 1000L
                val size = cursor.getLong(sizeColumn)
                val mime = cursor.getString(mimeColumn)
                val relativePath = cursor.getString(relativePathColumn)

                val key = generateKey(
                    relativePath = relativePath,
                    name = title,
                    size = size,
                    duration = duration,
                    mime = mime
                )

                // Stores column values and the contentUri in a local object
                // that represents the media file.
                songs.plusAssign(
                    SongMediaStoreLocalDatasourceModel(
                        id =  id,
                        displayName = title,
                        duration =  duration,
                        artistId = artistId,
                        albumId = albumId,
                        genreId = genreId,
                        dateAdded = dateAdded,
                        key = key
                    )
                )
            }
        }

        return flowOf(songs.toList())
    }

    override fun getMediaItemFromId(id: Long): SongMetadataMediaStoreLocalDatasourceModel? {

        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            id
        )

        val projection = arrayOf(
            MediaStore.Audio.Media.DISPLAY_NAME,
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

            val nameColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            val titleColumn =
                cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)

            val artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)

            val durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)

            val albumIdColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)

            if (cursor.moveToFirst()) {

                //TODO rewrite with getColumnIndex and -1 checks

                val title = cursor.getString(titleColumn) ?: cursor.getString(nameColumn)
                    ?.substringBeforeLast(".") ?: DEFAULT_SONG_TITLE
                val artist = cursor.getString(artistColumn)?: DEFAULT_ARTIST_NAME
                val duration = cursor.getInt(durationColumn)
                val albumId = cursor.getLong(albumIdColumn)

                return SongMetadataMediaStoreLocalDatasourceModel(
                    displayName = title,
                    duration = duration,
                    author = artist,
                    albumId = albumId
                )
            }
        }
        return null
    }

    private fun getArtistString(raw: String): String {

        return if (raw == "<unknown>") context.getString(R.string.unknown_artist) else raw
    }

    override fun getListOfGenres(): Flow<List<GenreMediaStoreLocalDatasourceModel>> {

        val genres = mutableListOf<GenreMediaStoreLocalDatasourceModel>()

        val collection = MediaStore.Audio.Genres.EXTERNAL_CONTENT_URI

        val projection = arrayOf(

            MediaStore.Audio.Genres._ID,
            MediaStore.Audio.Genres.NAME
        )

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )

        query?.use { cursor ->

            //TODO rewrite with getColumnIndex and -1 checks
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Genres._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Genres.NAME)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)?: DEFAULT_GENRE_NAME

                val genre = GenreMediaStoreLocalDatasourceModel(
                    id = id,
                    name = name
                )

                genres.plusAssign(genre)
            }
        }

        return flowOf(genres)
    }

    override fun getListOfAlbums(): Flow<List<AlbumMediaStoreLocalDatasourceModel>> {

        val albums = mutableListOf<AlbumMediaStoreLocalDatasourceModel>()

        val collection = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        val projection = arrayOf(

            MediaStore.Audio.Albums.ALBUM_ID,
            MediaStore.Audio.Albums.ALBUM,
        )

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )

        query?.use { cursor ->

            //TODO rewrite with getColumnIndex and -1 checks
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)?: DEFAULT_ALBUM_NAME

                albums.plusAssign(
                    AlbumMediaStoreLocalDatasourceModel(
                        id = id,
                        name = name,
                    )
                )
            }
        }

        return flowOf(albums)
    }

    override fun getListOfArtists(): Flow<List<ArtistMediaStoreLocalDatasourceModel>> {

        val artists = mutableListOf<ArtistMediaStoreLocalDatasourceModel>()

        val collection = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI

        val projection = arrayOf(

            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST
        )

        val query = context.contentResolver.query(
            collection,
            projection,
            null,
            null,
            null
        )

        query?.use { cursor ->

            //TODO rewrite with getColumnIndex and -1 checks
            val idColumn = cursor.getColumnIndex(MediaStore.Audio.Artists._ID)
            val nameColumn = cursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST)

            while (cursor.moveToNext()) {

                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)?: DEFAULT_ARTIST_NAME

                artists.plusAssign(
                    ArtistMediaStoreLocalDatasourceModel(
                        id = id,
                        name = name
                    )
                )
            }
        }

        return flowOf(artists)
    }

    private fun generateKey(
        relativePath: String,
        name: String,
        size: Long,
        duration: Int,
        mime: String
    ): String {

        return "${relativePath}_${name}|${duration}_${mime}_${size}"
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