package com.example.core.ui.grid.model

import androidx.annotation.StringDef

@StringDef(
    GridItemId.HeaderId.Companion.HEADER/*, GridItemId.HeaderId.Companion.HEADER_PLAYLIST*/, GridItemId.ItemId.Companion.ITEM
)
annotation class GridItemId {

    annotation class HeaderId {
        companion object {
            const val HEADER = "HEADER"
            //const val HEADER_PLAYLIST = "HEADER_PLAYLIST"
        }
    }

    annotation class ItemId {
        companion object {
            const val ITEM = "ITEM"
        }
    }
}