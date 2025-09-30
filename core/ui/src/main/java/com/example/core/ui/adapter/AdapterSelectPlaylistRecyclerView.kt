package com.example.core.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.databinding.LayoutSelectPlaylistRecyclerViewItemBinding

class AdapterSelectPlaylistRecyclerView<T>(
    private val playlistsInfo: List<T>,
    private val idSelector: (T) -> Long,
    private val labelSelector: (T) -> String
):
RecyclerView.Adapter<AdapterSelectPlaylistRecyclerView.ViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {

        fun onClick(playlistId: Long)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = LayoutSelectPlaylistRecyclerViewItemBinding.inflate(
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

        val item = playlistsInfo[position]

        holder.binding.playlistName.setText(labelSelector(item))

        holder.binding.root.setOnClickListener { l ->

            onItemClickListener?.onClick(idSelector(item))
        }
    }

    override fun getItemCount(): Int {
        return playlistsInfo.size
    }

    class ViewHolder(val binding: LayoutSelectPlaylistRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)

    fun setOnItemClickListener(listener: OnItemClickListener) {

        this.onItemClickListener = listener
    }
}