package com.example.core.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.R
import com.example.core.ui.databinding.LayoutAllSongsRecyclerViewItemBinding
import com.example.core.ui.SongDiffCallback
import com.example.core.ui.model.SongInfoUIModel

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

        holder.binding.songTitle.setText(
            song.name
        )
        val minutes = (song.duration /1000) / 60

        val seconds = (song.duration /1000) % 60

        val duration = String.format("%02d:%02d", minutes, seconds)

        holder.binding.songDuration.setText(
            duration
        )

        holder.binding.songArtist.setText(
            song.artist
        )

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