/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Flagship Tactile Haptic Feedback Engine
 * Delivers crisp, low-latency, linear actuator vibrations (Click, Tick, HeavyClick)
 */
object HapticFeedbackHelper {
    @Volatile
    var isEnabled: Boolean = true

    /**
     * Crisp tactile click (e.g. Play/Pause, Favorite, Next, Prev, Button Tap)
     */
    fun click(context: Context) {
        if (!isEnabled) return
        vibrate(context, VibrationEffect.EFFECT_CLICK, fallbackDuration = 18L)
    }

    /**
     * Micro subtle tick (e.g. Seekbar scrubbing on second intervals, volume steps)
     */
    fun tick(context: Context) {
        if (!isEnabled) return
        vibrate(context, VibrationEffect.EFFECT_TICK, fallbackDuration = 10L)
    }

    /**
     * Heavy tactile pulse (e.g. Long-press action, menu popups)
     */
    fun heavyClick(context: Context) {
        if (!isEnabled) return
        vibrate(context, VibrationEffect.EFFECT_HEAVY_CLICK, fallbackDuration = 35L)
    }

    /**
     * Double pulse (e.g. Download complete, playlist created)
     */
    fun doubleClick(context: Context) {
        if (!isEnabled) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrate(context, VibrationEffect.EFFECT_DOUBLE_CLICK, fallbackDuration = 35L)
        } else {
            vibrate(context, -1, fallbackDuration = 35L)
        }
    }

    private fun vibrate(context: Context, effectId: Int, fallbackDuration: Long) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator == null || !vibrator.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && effectId != -1) {
                vibrator.vibrate(VibrationEffect.createPredefined(effectId))
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(fallbackDuration, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(fallbackDuration)
            }
        } catch (_: Exception) {}
    }
}

