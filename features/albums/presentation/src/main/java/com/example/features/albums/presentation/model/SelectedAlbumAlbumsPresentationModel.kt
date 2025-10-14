package com.example.features.albums.presentation.model

sealed interface SelectedAlbumAlbumsPresentationModel {

    data class Selected(
        val album: AlbumAlbumsPresentationModel
    ): SelectedAlbumAlbumsPresentationModel

    data object Default: SelectedAlbumAlbumsPresentationModel
}