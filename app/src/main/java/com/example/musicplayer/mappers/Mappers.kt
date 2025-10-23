package com.example.musicplayer.mappers

import com.example.features.albums.domain.model.AlbumAlbumsDomainModel
import com.example.musicplayer.models.AlbumMainPresentationModel

fun AlbumAlbumsDomainModel.toAlbumMainPresentationModel(): AlbumMainPresentationModel {

    return AlbumMainPresentationModel(
        id = this.id
    )
}