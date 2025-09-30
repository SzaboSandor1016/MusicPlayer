package com.example.core.ui.grid.holder

import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.databinding.LayoutGridItemBinding
import com.example.core.ui.grid.model.GridItem

class GridItemHolder(val binding: LayoutGridItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(gridItem: GridItem.Item) {

        binding.playlistTitle.setText(gridItem.label)
        binding.root.setOnClickListener { l ->

            gridItem.action?.invoke(gridItem.itemId)
        }

        binding.playAll.setOnClickListener { l ->

            gridItem.action1?.invoke(gridItem.itemId)
        }
    }
}