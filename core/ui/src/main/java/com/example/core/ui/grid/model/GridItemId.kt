package com.example.core.ui.grid.model

import androidx.annotation.StringDef

@StringDef(
    GridItemId.HeaderId.Companion.PLAYLIST_HEADER,
    GridItemId.HeaderId.Companion.SONG_HEADER,
    GridItemId.ItemId.Companion.SONG_ITEM,
    GridItemId.ItemId.Companion.ITEM
)
annotation class GridItemId {

    annotation class HeaderId {
        companion object {
            const val PLAYLIST_HEADER = "HEADER_PLAYLIST"
            const val SONG_HEADER = "HEADER_SONG"
        }
    }

    annotation class ItemId {
        companion object {
            const val ITEM = "ITEM"
            const val SONG_ITEM = "ROW_ITEM"
        }
    }

    /*annotation class RowItemId {
        companion object {

        }
    }*/
}