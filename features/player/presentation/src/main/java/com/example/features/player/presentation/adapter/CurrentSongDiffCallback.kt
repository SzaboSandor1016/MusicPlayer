package com.example.features.player.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.features.player.presentation.model.SongPlayerPresentationModel

class CurrentSongDiffCallback : DiffUtil.ItemCallback<SongPlayerPresentationModel>() {
        
        override fun areItemsTheSame(oldItem: SongPlayerPresentationModel, newItem: SongPlayerPresentationModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SongPlayerPresentationModel, newItem: SongPlayerPresentationModel): Boolean {
            return oldItem == newItem
        }
}