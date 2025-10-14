package com.example.core.ui.grid.holder

import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.databinding.LayoutGridSongItemBinding
import com.example.core.ui.grid.model.GridItem

class GridSongItemHolder(val binding: LayoutGridSongItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(gridSongItem: GridItem.SongItem) {

        binding.songArtist.text = gridSongItem.artist

        binding.songTitle.text = gridSongItem.title

        binding.songDuration.text = gridSongItem.duration.toString()

        binding.more.setOnClickListener(null)

        if (gridSongItem.actionAll != null) {

            binding.more.setOnClickListener { l ->

                gridSongItem.actionAll.invoke(gridSongItem.itemId, binding.root)
            }
        }

        binding.root.setOnClickListener(null)

        if (gridSongItem.action != null) {

            binding.root.setOnClickListener { l ->

                gridSongItem.action.invoke(gridSongItem.itemId)
            }
        }
    }
}