package com.example.musicplayer.sharedprefs

import android.content.SharedPreferences
import com.example.musicplayer.models.AudioEffectMainPresentationModel
import com.google.gson.Gson
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import androidx.core.content.edit

class EqualizerPreferences(
) {

    companion object {
        const val AUDIO_EFFECT_PREFERENCES = "audio_effect_preferences"

        private const val AUDIO_EFFECT_IS_EQUALIZER_ENABLED = "is_equalizer_enabled"
        private const val AUDIO_EFFECT_EQUALIZER_SETTING = "equalizer_audio_effect"
        private const val AUDIO_EFFECT_LOWEST_BAND_LEVEL = "equalizer_lowest_band_level"
    }

    private val sharedPreferences: SharedPreferences by inject(
        SharedPreferences::class.java,
        named(AUDIO_EFFECT_PREFERENCES)
    )

    private val gson: Gson by inject(Gson::class.java, named("GSON"))

    var isEqualizerEnabled: Boolean
        get() = sharedPreferences.getBoolean(AUDIO_EFFECT_IS_EQUALIZER_ENABLED, false)
        set(isEnable) = sharedPreferences.edit {
            putBoolean(AUDIO_EFFECT_IS_EQUALIZER_ENABLED, isEnable)
        }

    // Getting and setting the user's audio preferences
    var audioEffects: AudioEffectMainPresentationModel?
        get() {
            val json = sharedPreferences.getString(AUDIO_EFFECT_EQUALIZER_SETTING, null)
            if (json != null) {
                try {
                    return gson.fromJson(json, AudioEffectMainPresentationModel::class.java)
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
            return null
        }
        set(audioEffects) {
            var json: String? = null
            if (audioEffects != null) {
                json = gson.toJson(audioEffects)
            }
            sharedPreferences.edit { putString(AUDIO_EFFECT_EQUALIZER_SETTING, json) }
        }

    var lowestBandLevel: Int
        get() = sharedPreferences.getInt(AUDIO_EFFECT_LOWEST_BAND_LEVEL, 0)
        set(value) = sharedPreferences.edit { putInt(AUDIO_EFFECT_LOWEST_BAND_LEVEL, value) }
}