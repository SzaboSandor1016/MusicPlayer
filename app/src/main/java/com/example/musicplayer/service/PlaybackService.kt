package com.example.musicplayer.service

import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.features.songs.domain.usecase.AssembleSourceMediaItemsSyncUseCase
import com.example.musicplayer.sharedprefs.PlayerPreferences
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import org.koin.android.ext.android.inject

class PlaybackService: MediaSessionService() {

    private val playerPreferences: PlayerPreferences by inject<PlayerPreferences>()

    private val assembleSourceMediaItemsSyncUseCase: AssembleSourceMediaItemsSyncUseCase by inject<AssembleSourceMediaItemsSyncUseCase>()

    private lateinit var player: Player
    private var mediaSession: MediaSession? = null

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()
        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player)
            .setCallback(object : MediaSession.Callback {

                override fun onPlaybackResumption(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {

                    val restored = playerPreferences.playerState
                    val restoredShuffle = playerPreferences.isShuffleEnabled
                    val restoredRepeatMode = playerPreferences.repeatMode

                    session.player.repeatMode = restoredRepeatMode

                    session.player.shuffleModeEnabled = restoredShuffle

                    if (restored != null) {

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
                    return Futures.immediateFuture(
                        MediaSession.MediaItemsWithStartPosition(emptyList(), 0, 0L)
                    )
                }
            })
            .build()
    }

    override fun onDestroy() {

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

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        mediaSession?.player?.let { p ->
            if (p.playWhenReady) p.pause()
        }
        stopSelf()
    }
}