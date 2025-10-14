package com.example.core.ui.grid.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.databinding.LayoutGridItemBinding
import com.example.core.ui.grid.model.GridItem

class GridItemHolder(val binding: LayoutGridItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(gridItem: GridItem.Item) {

        binding.playlistTitle.setText(gridItem.label)

        binding.playAll.visibility = View.GONE

        binding.playAll.setOnClickListener(null)

        binding.root.setOnClickListener { l ->

            gridItem.action?.invoke(gridItem.itemId)
        }

        if (gridItem.actionAll != null) {

            binding.playAll.visibility = View.VISIBLE

            binding.playAll.setOnClickListener { l ->

                gridItem.actionAll.invoke(gridItem.itemId)
            }
        }
    }
}