package com.example.features.playlists.presentation.models

sealed interface SelectedPlaylistPlaylistsPresentationModel {

    data object Default: SelectedPlaylistPlaylistsPresentationModel

    data class Selected(
        val playlist: PlaylistPlaylistsPresentationModel
    ): SelectedPlaylistPlaylistsPresentationModel
}