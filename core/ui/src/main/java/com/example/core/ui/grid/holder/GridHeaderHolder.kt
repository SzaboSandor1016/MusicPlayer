package com.example.core.ui.grid.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.databinding.LayoutGridHeaderBinding
import com.example.core.ui.grid.model.GridItem

class GridHeaderHolder(val binding: LayoutGridHeaderBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(item: GridItem.Header) {

        binding.title.setText(item.titleId)

        if (item.actionId != null) {

            binding.actionButton.visibility = View.VISIBLE

            binding.newPlaylist.setText(item.actionId)

            binding.newPlaylist.setOnClickListener { l ->

                item.action?.invoke()
            }
        } else {

            binding.actionButton.visibility = View.GONE
        }
    }
}