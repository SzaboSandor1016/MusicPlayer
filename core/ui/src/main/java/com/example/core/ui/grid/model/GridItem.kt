package com.example.core.ui.grid.model

import androidx.annotation.StringRes
import com.example.core.ui.R

sealed class GridItem(val id: String, val type: GridItemType) {

    data class Header(
        val action: (() -> Unit)? = null,
        @StringRes val titleId: Int,
        @StringRes val actionId: Int? = null
    ): GridItem(GridItemId.HeaderId.Companion.HEADER,GridItemType.HEADER) {
    }

    data class Item(
        val action: ((Long) -> Unit)? = null,
        val action1: ((Long) -> Unit)? = null,
        val itemId: Long,
        val label: String
    ): GridItem(GridItemId.ItemId.Companion.ITEM,GridItemType.ITEM)
}