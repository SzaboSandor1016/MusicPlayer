package com.example.core.ui

import androidx.recyclerview.widget.DiffUtil
import com.example.core.ui.model.SongInfoUIModel

class SongDiffCallback : DiffUtil.ItemCallback<SongInfoUIModel>() {

        override fun areItemsTheSame(oldItem: SongInfoUIModel, newItem: SongInfoUIModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SongInfoUIModel, newItem: SongInfoUIModel): Boolean {
            return oldItem == newItem
        }
    }