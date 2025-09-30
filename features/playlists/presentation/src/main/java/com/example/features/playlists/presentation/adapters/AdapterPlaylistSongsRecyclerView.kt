package com.example.features.playlists.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.features.playlists.presentation.databinding.LayoutPlaylistSongsRecyclerViewItemBinding
import com.example.features.playlists.presentation.models.SongPlaylistsPresentationModel

class AdapterPlaylistSongsRecyclerView(
    private val songs: List<SongPlaylistsPresentationModel>
): RecyclerView.Adapter<AdapterPlaylistSongsRecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    interface OnClickListener {

        fun onClick(song: SongPlaylistsPresentationModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = LayoutPlaylistSongsRecyclerViewItemBinding.inflate(
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

        val item = songs[position]

        holder.binding.songTitle.setText(item.displayName)

        val minutes = (item.duration /1000) / 60

        val seconds = (item.duration /1000) % 60

        val duration = String.format("%02d:%02d", minutes, seconds)

        holder.binding.songDuration.setText(duration)

        holder.binding.songArtist.setText(item.author)

        holder.binding.root.setOnClickListener { l ->

            onClickListener?.onClick(
                item
            )
        }
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    class ViewHolder(val binding: LayoutPlaylistSongsRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)

    fun setOnClickListener(listener: OnClickListener) {

        this.onClickListener = listener
    }
}