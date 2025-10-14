package com.example.core.ui.grid.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.databinding.LayoutGridSongHeaderBinding
import com.example.core.ui.grid.model.GridItem

class GridSongHeaderHolder(val binding: LayoutGridSongHeaderBinding):
    RecyclerView.ViewHolder(binding.root) {

    fun bind(gridSongHeader: GridItem.SongHeader) {

        binding.title.setText(gridSongHeader.titleId)

        binding.actionButton.visibility = View.GONE

        binding.playAll.setOnClickListener(null)

        if (gridSongHeader.action != null) {

            binding.actionButton.visibility = View.VISIBLE

            binding.playAll.setOnClickListener { l ->

                gridSongHeader.action.invoke()
            }
        }
    }
}