package com.example.musicplayer.adapter

import android.graphics.drawable.Animatable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.R
import com.example.musicplayer.databinding.LayoutCurrentSourceRecyclerViewItemBinding
import com.example.musicplayer.models.SongMainPresentationModel

class AdapterCurrentSourceRecyclerView:
    ListAdapter<SongMainPresentationModel, AdapterCurrentSourceRecyclerView.ViewHolder>(CurrentSongDiffCallback()){

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

        val binding = LayoutCurrentSourceRecyclerViewItemBinding.inflate(
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

        holder.binding.upNext.visibility = View.GONE

        (holder.binding.playedIcon.drawable as? Animatable)?.stop()

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
            song.author
        )

        if (song.current) {
            holder.binding.root.setBackgroundResource(R.drawable.shape_current_song_recycler_view_background)
            (holder.binding.playedIcon.drawable as? Animatable)?.start()
        }

        if (song.isUpNext) {
            holder.binding.root.setBackgroundResource(R.drawable.shape_up_next_song_recycler_view_background)
            holder.binding.upNext.visibility = View.VISIBLE
        } else if (currentList.indexOf(song) % 2 == 1) {

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

        //holder.bind(getItem(position),position)
    }

    inner class ViewHolder(
        val binding: LayoutCurrentSourceRecyclerViewItemBinding
    ): RecyclerView.ViewHolder(binding.root) {

        /*fun bind(song: SongMainPresentationModel, position: Int) {


        }*/
    }

    fun setOnClickListener(listener: ((Long) -> Unit)) {

        this.onClickListener = listener
    }

    fun setOnMoreOptionsClickListener(listener: ((View, Long) -> Unit)) {

        this.onMoreOptionsClickListener = listener
    }
}