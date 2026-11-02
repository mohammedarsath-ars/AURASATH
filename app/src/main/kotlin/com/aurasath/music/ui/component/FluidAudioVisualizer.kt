/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.sin

/**
 * AURASATH Fluid Neon Audio Visualizer.
 * Renders live synchronized pulsating soundwave bars with dual-color gradient glow.
 */
@Composable
fun FluidAudioVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 28,
    maxHeight: Dp = 48.dp,
    primaryColor: Color = MaterialTheme.colorScheme.primary,
    accentColor: Color = remember { Color(0xFF00E5FF) }, // Neon Cyan
) {
    val transition = rememberInfiniteTransition(label = "visualizerAnim")

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vizPhase"
    )

    val gradientColors = remember(accentColor, primaryColor) {
        listOf(
            accentColor,
            primaryColor.copy(alpha = 0.85f),
            primaryColor.copy(alpha = 0.35f)
        )
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(maxHeight)
    ) {
        val totalWidth = size.width
        val canvasHeight = size.height
        if (totalWidth <= 0f || canvasHeight <= 0f) return@Canvas

        val barSpacing = 4.dp.toPx()
        val totalSpacing = barSpacing * (barCount - 1)
        val barWidth = ((totalWidth - totalSpacing) / barCount).coerceAtLeast(2f)

        val gradient = Brush.verticalGradient(
            colors = gradientColors,
            startY = 0f,
            endY = canvasHeight
        )

        for (i in 0 until barCount) {
            val normalizedIndex = i.toFloat() / barCount
            val wave1 = sin(phase + normalizedIndex * 4.5f)
            val wave2 = sin(phase * 1.6f + normalizedIndex * 8.0f)
            val wave3 = sin(phase * 0.7f - normalizedIndex * 3.2f)

            // Dynamic amplitude when playing, minimal resting ripple when paused
            val rawAmp = if (isPlaying) {
                (abs(wave1 * 0.45f + wave2 * 0.35f + wave3 * 0.20f)).coerceIn(0.12f, 1.0f)
            } else {
                0.08f + 0.04f * sin(phase + i)
            }

            // Bell-curve weighting so center bars are naturally taller
            val bellCurve = sin(normalizedIndex * 3.14159f).coerceIn(0.35f, 1.0f)
            val barHeight = (canvasHeight * rawAmp * bellCurve).coerceAtLeast(3.dp.toPx())

            val left = i * (barWidth + barSpacing)
            val top = (canvasHeight - barHeight) / 2f

            drawRoundRect(
                brush = gradient,
                topLeft = Offset(left, top),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

