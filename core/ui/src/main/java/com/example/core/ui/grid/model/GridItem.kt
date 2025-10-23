package com.example.core.ui.grid.model

import android.view.View
import androidx.annotation.StringRes

sealed class GridItem(val id: String, val type: GridItemType) {

    data class PlaylistHeader(
        val action: (() -> Unit)? = null,
        @StringRes val titleId: Int,
        @StringRes val actionId: Int? = null
    ): GridItem(GridItemId.HeaderId.Companion.PLAYLIST_HEADER,GridItemType.PLAYLIST_HEADER) {

    }

    data class SongHeader(
        val action: (() -> Unit)? = null,
        @StringRes val titleId: Int,
        @StringRes val actionId: Int? = null
    ): GridItem(GridItemId.HeaderId.Companion.SONG_HEADER,GridItemType.SONG_HEADER) {

    }

    data class Item(
        val action: ((Long) -> Unit)? = null,
        val actionAll: ((Long) -> Unit)? = null,
        val itemId: Long,
        val albumId: Long = -1L,
        val label: String
    ): GridItem(GridItemId.ItemId.Companion.ITEM,GridItemType.ITEM)

    data class SongItem(
        val action: ((Long) -> Unit)? = null,
        val actionAll: ((Long, View) -> Unit)? = null,
        val itemId: Long,
        val albumId: Long,
        val title: String,
        val duration: Int,
        val artist: String
    ): GridItem(GridItemId.ItemId.Companion.SONG_ITEM, GridItemType.SONG_ITEM)
}