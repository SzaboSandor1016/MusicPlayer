package com.example.musicplayer.models

import android.media.audiofx.BassBoost
import android.media.audiofx.Virtualizer

data class AudioEffectMainPresentationModel(
    val selectedEffectType: Int,
    val gainValues: ArrayList<Double>
) {
}