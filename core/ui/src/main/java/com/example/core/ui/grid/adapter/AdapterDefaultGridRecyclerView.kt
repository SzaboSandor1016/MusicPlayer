package com.example.core.ui.grid.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core.ui.databinding.LayoutGridItemBinding
import com.example.core.ui.databinding.LayoutGridPlaylistHeaderBinding
import com.example.core.ui.databinding.LayoutGridSongHeaderBinding
import com.example.core.ui.databinding.LayoutGridSongItemBinding
import com.example.core.ui.grid.diff.GridDiff
import com.example.core.ui.grid.holder.GridHeaderHolder
import com.example.core.ui.grid.holder.GridItemHolder
import com.example.core.ui.grid.holder.GridSongHeaderHolder
import com.example.core.ui.grid.holder.GridSongItemHolder
import com.example.core.ui.grid.model.GridItem
import com.example.core.ui.grid.model.GridItemType

class AdapterDefaultGridRecyclerView(
): DefaultDiffAdapter<GridItem, GridDiff, RecyclerView.ViewHolder>() {

    override val diff: GridDiff get() = GridDiff()

    override fun setList(list: List<GridItem>): DefaultDiffAdapter<GridItem, GridDiff, RecyclerView.ViewHolder> = apply {

        super.setList(list)

        this.list.clear()
        this.list.addAll(list.map { it })
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {

        return when(GridItemType.entries[viewType]) {
            GridItemType.PLAYLIST_HEADER -> GridHeaderHolder(LayoutGridPlaylistHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
            GridItemType.SONG_HEADER-> GridSongHeaderHolder(LayoutGridSongHeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
            GridItemType.ITEM -> GridItemHolder(LayoutGridItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))

            GridItemType.SONG_ITEM -> GridSongItemHolder(LayoutGridSongItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = list.getOrNull(position)

        when(item) {
            is GridItem.PlaylistHeader -> (holder as? GridHeaderHolder)?.bind(item)
            is GridItem.SongHeader -> (holder as? GridSongHeaderHolder)?.bind(item)
            is GridItem.Item -> (holder as? GridItemHolder)?.bind(item)
            is GridItem.SongItem -> (holder as? GridSongItemHolder)?.bind(item, position)
            null -> {}
        }
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type.ordinal
    }
}