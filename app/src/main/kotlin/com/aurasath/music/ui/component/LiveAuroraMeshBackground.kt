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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.aurasath.music.ui.theme.rememberGlobalAccentColors
import kotlin.math.cos
import kotlin.math.sin

/**
 * AURASATH Live Fluid Aurora Mesh Engine.
 * 
 * Generates an organic, continuously undulating 4-point dynamic mesh aura
 * with multi-harmonic sinusoidal movement. Renders with pure GPU canvas
 * radial gradients for 120Hz liquid smoothness with minimal battery drain.
 */
@Composable
fun LiveAuroraMeshBackground(
    modifier: Modifier = Modifier,
    baseColor: Color = Color(0xFF07080D), // Pure Obsidian Dark
    primaryAccent: Color = rememberGlobalAccentColors().first,
    secondaryAccent: Color = MaterialTheme.colorScheme.tertiary,
    intensity: Float = 0.35f,
) {
    val transition = rememberInfiniteTransition(label = "auroraTransition")

    // Harmonic wave phases
    val phase1 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase1"
    )

    val phase2 by transition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 24000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase2"
    )

    val electricCyan = remember { Color(0xFF00E5FF) }
    val deepViolet = remember { Color(0xFF7C4DFF) }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@Canvas

            // Base fill
            drawRect(color = baseColor)

            // Orb 1: Primary Accent (Top-Left fluid movement)
            val orb1X = width * (0.28f + 0.16f * sin(phase1))
            val orb1Y = height * (0.22f + 0.12f * cos(phase2))
            val orb1Radius = width * 0.75f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryAccent.copy(alpha = 0.28f * intensity),
                        primaryAccent.copy(alpha = 0.12f * intensity),
                        Color.Transparent
                    ),
                    center = Offset(orb1X, orb1Y),
                    radius = orb1Radius
                ),
                radius = orb1Radius,
                center = Offset(orb1X, orb1Y)
            )

            // Orb 2: Deep Violet / Tertiary (Top-Right fluid movement)
            val orb2X = width * (0.74f + 0.14f * cos(phase1))
            val orb2Y = height * (0.35f + 0.15f * sin(phase2))
            val orb2Radius = width * 0.85f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        deepViolet.copy(alpha = 0.24f * intensity),
                        secondaryAccent.copy(alpha = 0.10f * intensity),
                        Color.Transparent
                    ),
                    center = Offset(orb2X, orb2Y),
                    radius = orb2Radius
                ),
                radius = orb2Radius,
                center = Offset(orb2X, orb2Y)
            )

            // Orb 3: Electric Cyan (Mid-Center harmonic drift)
            val orb3X = width * (0.50f + 0.20f * sin(phase2 * 0.8f))
            val orb3Y = height * (0.55f + 0.18f * cos(phase1 * 0.8f))
            val orb3Radius = width * 0.65f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        electricCyan.copy(alpha = 0.18f * intensity),
                        Color.Transparent
                    ),
                    center = Offset(orb3X, orb3Y),
                    radius = orb3Radius
                ),
                radius = orb3Radius,
                center = Offset(orb3X, orb3Y)
            )
        }
    }
}

