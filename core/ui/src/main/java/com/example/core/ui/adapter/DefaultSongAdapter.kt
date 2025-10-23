package com.example.core.ui.adapter

import android.content.ContentUris
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.common.values.DEFAULT_ARTIST_NAMES
import com.example.core.ui.R
import com.example.core.ui.databinding.LayoutAllSongsRecyclerViewItemBinding
import com.example.core.ui.SongDiffCallback
import com.example.core.ui.model.SongInfoUIModel
import androidx.core.net.toUri

open class DefaultSongAdapter/*(private val songs: List<SongInfoUIModel>)*/:
ListAdapter<SongInfoUIModel,DefaultSongAdapter.ViewHolder>(SongDiffCallback()){

    private var onClickListener: ( (Long)-> Unit)? = null

    private var onMoreOptionsClickListener: ((View, Long)-> Unit)? = null
    /*interface OnClickListener {

        fun onClick(songId: Long)
    }

    interface OnMoreOptionsClickListener {

        fun onClick(songId: Long)
    }*/

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = LayoutAllSongsRecyclerViewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val song = getItem(position)

        holder.binding.root.background = null

        holder.binding.playedIcon.setImageResource(R.drawable.ic_music_note_single_36)

        holder.binding.playedIcon.clipToOutline = true

        holder.binding.songTitle.setText(
            song.name
        )
        val minutes = (song.duration /1000) / 60

        val seconds = (song.duration /1000) % 60

        val duration = String.format("%02d:%02d", minutes, seconds)

        holder.binding.songDuration.setText(
            duration
        )

        if (song.artist !in DEFAULT_ARTIST_NAMES) {
            holder.binding.songArtist.setText(
                song.artist
            )
        } else {
            holder.binding.songArtist.setText(
                R.string.unknown_artist
            )
        }

        /*if (song.current) {
            (holder.binding.playedIcon.drawable as? Animatable)?.start()
        } else {
            (holder.binding.playedIcon.drawable as? Animatable)?.stop()
        }*/

        if (currentList.indexOf(song) % 2 == 1) {

            holder.binding.root.setBackgroundResource(R.drawable.shape_default_song_recycler_view_background)
        }

        holder.binding.root.setOnClickListener { l ->

            onClickListener?.invoke(
                song.id
            )
        }

        holder.binding.more.setOnClickListener { l ->

            onMoreOptionsClickListener?.invoke(
                holder.binding.root,
                song.id
            )
        }

        val uri = ContentUris.withAppendedId(
            "content://media/external/audio/albumart".toUri(), song.albumId
        )

        Glide.with(
            holder.binding.playedIcon
        ).load(
            uri
        )/*.onlyRetrieveFromCache(
            true
        )*/.placeholder(
            R.drawable.ic_music_note_single_36
        ).error(
            R.drawable.ic_music_note_single_36
        ).into(
            holder.binding.playedIcon
        )

        //holder.bind(getItem(position), position)
    }

    inner class ViewHolder(
        val binding: LayoutAllSongsRecyclerViewItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        /*fun bind(song: SongInfoUIModel, position: Int) {

            binding.songTitle.setText(
                song.name
            )
            val minutes = (song.duration /1000) / 60

            val seconds = (song.duration /1000) % 60

            val duration = String.format("%02d:%02d", minutes, seconds)

            binding.songDuration.setText(
                duration
            )

            binding.songArtist.setText(
                song.artist
            )

            if (position % 2 == 1) {

                binding.root.setBackgroundResource(R.drawable.shape_default_song_recycler_view_background)
            }

            binding.root.setOnClickListener { l ->

                onClickListener?.invoke(
                    song.id
                )
            }

            binding.more.setOnClickListener { l ->

                onMoreOptionsClickListener?.invoke(
                    binding.root,
                    song.id
                )
            }
        }*/
    }

    fun setOnClickListener(listener: ((Long) -> Unit)) {

        this.onClickListener = listener
    }

    fun setOnMoreOptionsClickListener(listener: ((View, Long) -> Unit)) {

        this.onMoreOptionsClickListener = listener
    }
}