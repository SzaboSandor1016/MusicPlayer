package com.example.features.player.presentation.values
// Equalizer presets and gain values
val effectType = arrayListOf(
    "Custom", "Flat", "Acoustic", "Dance",
    "Hip Hop", "Jazz", "Pop", "Rock", "Podcast"
)

// Constants for presets
const val PRESET_CUSTOM = 0
const val PRESET_FLAT = 1
const val PRESET_ACOUSTIC = 2
const val PRESET_DANCE_LOUNGE = 3
const val PRESET_HIP_HOP = 4
const val PRESET_JAZZ_BLUES = 5
const val PRESET_POP = 6
const val PRESET_ROCK = 7
const val PRESET_PODCAST = 8

// Gain values for each preset
val FLAT = arrayListOf(0.0, 0.0, 0.0, 0.0, 0.0)
val ACOUSTIC = arrayListOf(0.44, 0.12, 0.12, 0.34, 0.2)
val DANCE = arrayListOf(0.52, 0.08, 0.28, 0.48, 0.06)
val HIP_HOPE = arrayListOf(0.44, 0.06, -0.14, 0.1, 0.38)
val JAZZ = arrayListOf(0.32, 0.0, 0.22, 0.1, 0.2)
val POP = arrayListOf(-0.14, 0.28, 0.38, 0.22, -0.2)
val ROCK = arrayListOf(0.38, 0.2, -0.04, 0.02, 0.34)
val PODCAST = arrayListOf(-0.12, 0.26, 0.36, 0.16, -0.2)