package com.example.features.player.presentation.service

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.features.songs.domain.usecase.AssembleSourceMediaItemsSyncUseCase
import com.example.features.player.presentation.sharedprefs.PlayerPreferences
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class PlaybackService: MediaSessionService() {

    private val playerPreferences: PlayerPreferences by inject<PlayerPreferences>()

    private val assembleSourceMediaItemsSyncUseCase: AssembleSourceMediaItemsSyncUseCase by inject<AssembleSourceMediaItemsSyncUseCase>()

    private val progressRefreshDelay = 1000L

    private var progressJob: Job? = null

    private var looperScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private lateinit var player: Player
    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()

        player.addListener(object: Player.Listener {

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                super.onMediaItemTransition(mediaItem, reason)

                playerPreferences.updateCurrentIndex(
                    index = player.currentMediaItemIndex
                )
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)

                if (isPlaying) {
                    startProgressLoop()
                } else {
                    stopProgressLoop()
                }
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
                super.onRepeatModeChanged(repeatMode)

                playerPreferences.updateRepeatMode(repeatMode)
            }

            override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                super.onShuffleModeEnabledChanged(shuffleModeEnabled)

                playerPreferences.updateShuffleEnabled(shuffleModeEnabled)
            }
        })

        setMediaNotificationProvider(DefaultMediaNotificationProvider(this))

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(object : MediaSession.Callback {

                override fun onPlaybackResumption(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {

                    val restored = playerPreferences.getPlayerStatePreference()
                    val restoredShuffle = playerPreferences.getShuffleModePreference()
                    val restoredRepeatMode = playerPreferences.getRepeatModePreference()

                    session.player.repeatMode = restoredRepeatMode

                    session.player.shuffleModeEnabled = restoredShuffle

                    val mediaItems =
                        assembleSourceMediaItemsSyncUseCase(restored.ids.toList())

                    return Futures.immediateFuture(
                        MediaSession.MediaItemsWithStartPosition(
                            mediaItems,
                            restored.currentIndex,
                            restored.position
                        )
                    )
                }
            })
            .build()
    }

    override fun onDestroy() {

        stopProgressLoop()

        mediaSession?.run {

            release()

            player.release()
        }

        mediaSession = null

        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {

        return mediaSession
    }

    /*override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSession?.player?.let { p ->
            if (p.playWhenReady) p.pause()
        }
        //stopSelf()
    }*/

    private fun startProgressLoop() {

        progressJob?.cancel()

        progressJob =  looperScope.launch {

            while(isActive) {

                val currentPositionMs = player.currentPosition

                playerPreferences.updatePosition(currentPositionMs)

                delay(progressRefreshDelay)
            }
        }
    }

    private fun stopProgressLoop() {
        progressJob?.cancel()
        progressJob = null
    }
}