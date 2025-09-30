package com.example.musicplayer.sharedprefs

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import com.example.musicplayer.models.PlayerStateMainPresentationModel
import com.google.gson.Gson
import org.koin.core.qualifier.named
import org.koin.java.KoinJavaComponent.inject

class PlayerPreferences {

    companion object {

        const val PLAYER_PREFERENCES = "player_preferences"

        private const val PLAYER_STATE = "player_state"

        private const val IS_SHUFFLE_ENABLED = "is_shuffle_enabled"

        private const val REPEAT_MODE = "repeat_mode"
    }

    private val sharedPreferences: SharedPreferences by inject(
        SharedPreferences::class.java,
        named(PLAYER_PREFERENCES)
    )

    private val gson: Gson by inject(
        Gson::class.java,
        named("GSON")
    )

    var isShuffleEnabled: Boolean
        get() = sharedPreferences.getBoolean(IS_SHUFFLE_ENABLED, false)
        set(enabled) = sharedPreferences.edit {

            putBoolean(IS_SHUFFLE_ENABLED, enabled)
        }

    var repeatMode: Int
        get() = sharedPreferences.getInt(REPEAT_MODE, Player.REPEAT_MODE_ONE)
        set(mode) = sharedPreferences.edit {

            putInt(REPEAT_MODE, mode)
        }

    var playerState: PlayerStateMainPresentationModel?
        get() {

            val json = sharedPreferences.getString(PLAYER_STATE, null)

            if (json != null) {

                try {
                    return gson.fromJson(json, PlayerStateMainPresentationModel::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return null
        }
        set(value) {

            val json = gson.toJson(value, PlayerStateMainPresentationModel::class.java)

            sharedPreferences.edit {

                putString(PLAYER_STATE,json)
            }
        }
}