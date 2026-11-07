/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.playback

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes

class SleepTimer(
    private val scope: CoroutineScope,
    var player: Player,
) : Player.Listener {
    private var sleepTimerJob: Job? = null
    private var initialVolume: Float? = null

    var triggerTime by mutableLongStateOf(-1L)
        private set
    var pauseWhenSongEnd by mutableStateOf(false)
        private set
    val isActive: Boolean
        get() = triggerTime != -1L || pauseWhenSongEnd

    fun start(minute: Int) {
        clear()
        if (minute == -1) {
            pauseWhenSongEnd = true
            triggerTime = -1L
            sleepTimerJob = scope.launch {
                while (pauseWhenSongEnd) {
                    delay(500L)
                    val duration = player.duration
                    val currentPos = player.currentPosition
                    if (duration > 0 && currentPos > 0) {
                        val remaining = duration - currentPos
                        if (remaining in 1..8_000L) {
                            // Smart Sleep Fade: Smoothly ramp down volume for the last 8 seconds of the song
                            val startVolume = player.volume
                            initialVolume = startVolume
                            val fadeSteps = 32
                            val stepDelay = remaining / fadeSteps
                            for (step in 1..fadeSteps) {
                                delay(stepDelay)
                                if (!pauseWhenSongEnd) break
                                val factor = (1f - (step.toFloat() / fadeSteps)).coerceIn(0f, 1f)
                                player.volume = startVolume * factor
                            }
                            if (pauseWhenSongEnd) {
                                pauseWhenSongEnd = false
                                player.pause()
                                player.volume = startVolume
                                initialVolume = null
                            }
                            break
                        }
                    }
                }
            }
        } else {
            pauseWhenSongEnd = false
            val totalDurationMs = minute.minutes.inWholeMilliseconds
            triggerTime = System.currentTimeMillis() + totalDurationMs
            val fadeDurationMs = 10_000L.coerceAtMost(totalDurationMs)
            val waitDurationMs = totalDurationMs - fadeDurationMs

            sleepTimerJob = scope.launch {
                if (waitDurationMs > 0) {
                    delay(waitDurationMs)
                }

                // Smart Sleep Fade: Gradually ramp down volume over the last 10 seconds
                val startVolume = player.volume
                initialVolume = startVolume
                val steps = 40 // Every 250ms over 10 seconds for seamless audio tapering
                val stepDelayMs = fadeDurationMs / steps

                for (step in 1..steps) {
                    delay(stepDelayMs)
                    val factor = (1f - (step.toFloat() / steps)).coerceIn(0f, 1f)
                    player.volume = startVolume * factor
                }

                player.pause()
                // Restore volume so the next playback session starts at user's normal volume
                player.volume = startVolume
                initialVolume = null
                triggerTime = -1L
            }
        }
    }

    /**
     * Notify the sleep timer that a song transition has occurred outside of normal
     * player callbacks (e.g. during crossfade player swap). If "end of song" mode
     * is active, this will pause the player and deactivate the timer.
     */
    fun notifySongTransition() {
        if (pauseWhenSongEnd) {
            pauseWhenSongEnd = false
            initialVolume?.let {
                player.volume = it
                initialVolume = null
            }
            player.pause()
        }
    }

    fun clear() {
        sleepTimerJob?.cancel()
        sleepTimerJob = null
        initialVolume?.let {
            player.volume = it
            initialVolume = null
        }
        pauseWhenSongEnd = false
        triggerTime = -1L
    }

    override fun onMediaItemTransition(
        mediaItem: MediaItem?,
        reason: Int,
    ) {
        if (pauseWhenSongEnd) {
            pauseWhenSongEnd = false
            initialVolume?.let {
                player.volume = it
                initialVolume = null
            }
            player.pause()
        }
    }

    override fun onPlaybackStateChanged(
        @Player.State playbackState: Int,
    ) {
        if (playbackState == Player.STATE_ENDED && pauseWhenSongEnd) {
            pauseWhenSongEnd = false
            initialVolume?.let {
                player.volume = it
                initialVolume = null
            }
            player.pause()
        }
    }
}
