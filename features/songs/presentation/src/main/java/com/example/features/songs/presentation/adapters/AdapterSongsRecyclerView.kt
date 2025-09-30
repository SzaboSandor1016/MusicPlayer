package com.example.features.songs.presentation.adapters
/*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.features.songs.presentation.databinding.LayoutAllSongsRecyclerViewItemBinding
import com.example.features.songs.presentation.models.SongSongsPresentationModel

class AdapterSongsRecyclerView(
    private val songs: List<SongSongsPresentationModel>
): RecyclerView.Adapter<AdapterSongsRecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    private var onMoreOptionsClickListener: OnMoreOptionsClickListener? = null
    interface OnClickListener {

        fun onClick(songId: Long)
    }

    interface OnMoreOptionsClickListener {

        fun onClick(songId: Long)
    }

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

        val item = songs[position]

        holder.binding.songTitle.setText(
            item.name
        )
        val minutes = (item.duration /1000) / 60

        val seconds = (item.duration /1000) % 60

        val duration = String.format("%02d:%02d", minutes, seconds)

        holder.binding.songDuration.setText(
            duration
        )

        holder.binding.songArtist.setText(
            item.author
        )

        holder.binding.root.setOnClickListener { l ->

            onClickListener?.onClick(
                item.id
            )
        }

        holder.binding.more.setOnClickListener { l ->

            onMoreOptionsClickListener?.onClick(
                item.id
            )
        }
    }

    override fun getItemCount(): Int {

        return songs.size
    }

    class ViewHolder(val binding: LayoutAllSongsRecyclerViewItemBinding): RecyclerView.ViewHolder(binding.root)

    fun setOnClickListener(listener: OnClickListener) {

        this.onClickListener = listener
    }

    fun setOnMoreOptionsClickListener(listener: OnMoreOptionsClickListener) {

        this.onMoreOptionsClickListener = listener
    }
}*/
