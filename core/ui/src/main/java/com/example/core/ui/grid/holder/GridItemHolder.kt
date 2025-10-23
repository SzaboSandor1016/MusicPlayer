package com.example.core.ui.grid.holder

import android.content.ContentUris
import android.view.View
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.common.values.DEFAULT_ARTIST_NAMES
import com.example.core.ui.R
import com.example.core.ui.databinding.LayoutGridItemBinding
import com.example.core.ui.grid.model.GridItem

class GridItemHolder(val binding: LayoutGridItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(gridItem: GridItem.Item) {

        binding.playlistImage.setImageResource(R.drawable.ic_music_library_24)

        binding.playlistImage.clipToOutline = true

        if (gridItem.label !in DEFAULT_ARTIST_NAMES) {
            binding.playlistTitle.setText(
                gridItem.label
            )
        } else {
            binding.playlistTitle.setText(
                R.string.unknown
            )
        }

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

        val id = gridItem.let {

            if(it.albumId == -1L) {
                it.itemId
            }
            else it.albumId
        }

        val uri = ContentUris.withAppendedId(
            "content://media/external/audio/albumart".toUri(), id
        )

        Glide.with(
            binding.playlistImage
        ).load(
            uri
        )/*.onlyRetrieveFromCache(
            true
        )*/.placeholder(
            R.drawable.ic_music_library_24
        ).error(
            R.drawable.ic_music_library_24
        ).into(
            binding.playlistImage
        )
    }
}