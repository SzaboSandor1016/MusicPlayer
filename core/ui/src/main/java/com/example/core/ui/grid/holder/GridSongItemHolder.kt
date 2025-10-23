package com.example.core.ui.grid.holder

import android.content.ContentUris
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.common.values.DEFAULT_ARTIST_NAMES
import com.example.core.ui.R
import com.example.core.ui.databinding.LayoutGridSongItemBinding
import com.example.core.ui.grid.model.GridItem

class GridSongItemHolder(val binding: LayoutGridSongItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(gridSongItem: GridItem.SongItem, position: Int) {

        binding.more.setOnClickListener(null)

        binding.root.background = null

        binding.playedIcon.setImageResource(R.drawable.ic_music_note_single_36)

        binding.playedIcon.clipToOutline = true

        if (gridSongItem.artist !in DEFAULT_ARTIST_NAMES) {
            binding.songArtist.setText(
                gridSongItem.artist
            )
        } else {
            binding.songArtist.setText(
                R.string.unknown_artist
            )
        }

        binding.songTitle.text = gridSongItem.title

        val minutes = (gridSongItem.duration /1000) / 60

        val seconds = (gridSongItem.duration /1000) % 60

        val duration = String.format("%02d:%02d", minutes, seconds)

        binding.songDuration.text = duration

        if (position % 2 == 1) {

            binding.root.setBackgroundResource(R.drawable.shape_default_song_recycler_view_background)
        }

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

        val uri = ContentUris.withAppendedId(
            "content://media/external/audio/albumart".toUri(), gridSongItem.albumId
        )

        Glide.with(
            binding.playedIcon
        ).load(
            uri
        )/*.onlyRetrieveFromCache(
            true
        )*/.placeholder(
            R.drawable.ic_music_note_single_36
        ).error(
            R.drawable.ic_music_note_single_36
        ).into(
            binding.playedIcon
        )
    }
}