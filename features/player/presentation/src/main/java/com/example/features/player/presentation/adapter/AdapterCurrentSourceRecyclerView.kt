package com.example.features.player.presentation.adapter

import android.content.ContentUris
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.core.common.values.DEFAULT_ARTIST_NAMES
import com.example.core.ui.R
import com.example.features.player.presentation.databinding.LayoutCurrentSourceRecyclerViewItemBinding
import com.example.features.player.presentation.model.SongPlayerPresentationModel

class AdapterCurrentSourceRecyclerView:
    ListAdapter<SongPlayerPresentationModel, AdapterCurrentSourceRecyclerView.ViewHolder>(CurrentSongDiffCallback()){

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
        holder.binding.more.setIconTintResource(R.color.primary)

        holder.binding.playedIcon.setImageResource(R.drawable.ic_music_note_single_36)

        holder.binding.playedIcon.clipToOutline = true

        holder.binding.upNext.visibility = View.GONE

        /*val contentUri: Uri =
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                song.id
            )

        try {
            Glide.with(holder.binding.root)
                .load(contentUri)
                .into(holder.binding.playedIcon)
        } catch (e: Exception) {
            holder.binding.playedIcon.setImageResource(R.drawable.ic_music_note_single_36)
        }*/

        /*(holder.binding.playedIcon.drawable as? Animatable)?.stop()*/

        holder.binding.songTitle.setText(
            song.name
        )
        val minutes = (song.duration /1000) / 60

        val seconds = (song.duration /1000) % 60

        val duration = String.format("%02d:%02d", minutes, seconds)

        holder.binding.songDuration.setText(
            duration
        )

        if (song.author !in DEFAULT_ARTIST_NAMES) {
            holder.binding.songArtist.setText(
                song.author
            )
        } else {
            holder.binding.songArtist.setText(
                R.string.unknown_artist
            )
        }

        if (song.current) {

            holder.binding.root.setBackgroundResource(R.drawable.shape_current_song_recycler_view_background)
            holder.binding.more.setIconTintResource(R.color.current_song_icon_tint)
            //(holder.binding.playedIcon.drawable as? Animatable)?.start()
        } else if (song.isUpNext) {

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

        Glide.with(
            holder.binding.playedIcon
        ).load(
            song.albumArtworkUri
        )/*.onlyRetrieveFromCache(
            true
        )*/.placeholder(
            R.drawable.ic_music_note_single_36
        ).error(
            R.drawable.ic_music_note_single_36
        ).into(
            holder.binding.playedIcon
        )

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