package com.example.core.ui.grid.diff

import com.example.core.ui.grid.model.GridItem

class GridDiff: DefaultDiff<GridItem>() {
    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = getOldItem(oldItemPosition) ?: return false
        val newItem = getNewItem(newItemPosition) ?: return false

        return oldItem.id == newItem.id && oldItem.type == newItem.type
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = getOldItem(oldItemPosition) ?: return false
        val newItem = getNewItem(newItemPosition) ?: return false

        return oldItem == newItem
    }
}