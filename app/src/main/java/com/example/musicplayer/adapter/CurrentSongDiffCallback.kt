package com.example.musicplayer.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.musicplayer.models.SongMainPresentationModel

class CurrentSongDiffCallback : DiffUtil.ItemCallback<SongMainPresentationModel>() {
        
        override fun areItemsTheSame(oldItem: SongMainPresentationModel, newItem: SongMainPresentationModel): Boolean {
            return oldItem.id == newItem.id && oldItem.current == newItem.current
        }

        override fun areContentsTheSame(oldItem: SongMainPresentationModel, newItem: SongMainPresentationModel): Boolean {
            return oldItem == newItem
        }
    }