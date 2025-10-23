package com.example.features.player.presentation.sharedprefs

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.media3.common.Player
import com.example.features.player.presentation.model.PlayerStatePlayerPresentationModel
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

    private var isShuffleEnabled: Boolean
        get() = sharedPreferences.getBoolean(IS_SHUFFLE_ENABLED, false)
        set(enabled) = sharedPreferences.edit {

            putBoolean(IS_SHUFFLE_ENABLED, enabled)
        }

    private var repeatMode: Int
        get() = sharedPreferences.getInt(REPEAT_MODE, Player.REPEAT_MODE_ONE)
        set(mode) = sharedPreferences.edit {

            putInt(REPEAT_MODE, mode)
        }

    private var playerState: PlayerStatePlayerPresentationModel?
        get() {

            val json = sharedPreferences.getString(PLAYER_STATE, null)

            if (json != null) {

                try {
                    return gson.fromJson(json, PlayerStatePlayerPresentationModel::class.java)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return null
        }
        set(value) {

            val json = gson.toJson(value, PlayerStatePlayerPresentationModel::class.java)

            sharedPreferences.edit {

                putString(PLAYER_STATE,json)
            }
        }

    fun getPlayerStatePreference(): PlayerStatePlayerPresentationModel {

        val current = playerState

        return if (current == null) {
            val default = PlayerStatePlayerPresentationModel(
                currentIndex = 0,
                displayText = "",
                position = 0,
                ids = emptySet()
            )

            playerState = default

            default
        } else {
            current
        }
    }

    fun getShuffleModePreference(): Boolean = isShuffleEnabled

    fun getRepeatModePreference(): Int = repeatMode

    fun updatePosition(position: Long) {
        val current = playerState ?: return
        playerState = current.copy(position = position)
    }

    fun updateCurrentIndex(index: Int) {
        val current = playerState ?: return
        playerState = current.copy(currentIndex = index)
    }

    fun updateQueue(ids: Set<Long>) {
        val current = playerState ?: PlayerStatePlayerPresentationModel(
            currentIndex = 0,
            displayText = "",
            position = 0,
            ids = emptySet()
        )
        playerState = current.copy(ids = ids)
    }

    fun updateRepeatMode(mode: Int) {

        repeatMode = mode
    }

    fun updateShuffleEnabled(enabled: Boolean) {

        isShuffleEnabled = enabled
    }
}