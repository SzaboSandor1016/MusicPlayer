package com.example.features.player.presentation.sharedprefs

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.features.player.presentation.model.BassBoostVirtualizerEffectPlayerUIModel
import com.google.gson.Gson
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject
import kotlin.getValue

class BassBoostVirtualizerPreferences {

    companion object {

        const val BASS_BOOST_VIRTUALIZER_PREFERENCES = "bass_boost_virtualizer_preferences"

        private const val AUDIO_EFFECT_BASS_BOOST_VIRTUALIZER = "bass_boost_level"
    }

    private val sharedPreferences: SharedPreferences by inject(
        SharedPreferences::class.java,
        named(BASS_BOOST_VIRTUALIZER_PREFERENCES
        )
    )

    private val gson: Gson by inject(Gson::class.java, named("GSON"))

    var bassBoostEffects: BassBoostVirtualizerEffectPlayerUIModel?
        get() {
            val json = sharedPreferences.getString(AUDIO_EFFECT_BASS_BOOST_VIRTUALIZER, null)
            if (json != null) {
                try {
                    return gson.fromJson(json, BassBoostVirtualizerEffectPlayerUIModel::class.java)
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
            sharedPreferences.edit { putString(AUDIO_EFFECT_BASS_BOOST_VIRTUALIZER, json) }
        }
}