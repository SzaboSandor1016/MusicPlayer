package com.example.features.playlists.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core.common.values.FAVORITES_NAME
import com.example.core.common.values.RECENT_NAME
import com.example.core.ui.R
import com.example.features.playlists.presentation.databinding.LayoutPlaylistsRecyclerViewItemBinding
import com.example.features.playlists.presentation.models.PlaylistPlaylistsPresentationModel

class AdapterPlaylistsRecyclerView(
    private val playlists: List<PlaylistPlaylistsPresentationModel>
): RecyclerView.Adapter<AdapterPlaylistsRecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    interface OnClickListener {

        fun onClick(playlist: PlaylistPlaylistsPresentationModel)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = LayoutPlaylistsRecyclerViewItemBinding.inflate(
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

        val item = playlists[position]

        val label = when(item.label) {

            FAVORITES_NAME -> R.string.favorites
            RECENT_NAME -> R.string.recent
            else -> Integer.MIN_VALUE
        }

        if (label == Integer.MIN_VALUE) {

            holder.binding.playlistTitle.setText(
                item.label
            )
        }else {
            holder.binding.playlistTitle.setText(
                label
            )
        }

        holder.binding.root.setOnClickListener { l ->

            onClickListener?.onClick(
                item
            )
        }
    }

    override fun getItemCount(): Int {

        return playlists.size
    }

    class ViewHolder(val binding: LayoutPlaylistsRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)

    fun setOnClickListener(listener: OnClickListener) {

        this.onClickListener = listener
    }
}