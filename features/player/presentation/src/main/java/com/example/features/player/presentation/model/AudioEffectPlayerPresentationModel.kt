package com.example.features.player.presentation.model

data class AudioEffectPlayerPresentationModel(
    val selectedEffectType: Int,
    val gainValues: ArrayList<Double>
) {
}