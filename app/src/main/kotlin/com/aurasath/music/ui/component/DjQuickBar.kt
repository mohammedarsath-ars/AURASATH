/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.PlaybackParameters
import com.aurasath.music.playback.PlayerConnection
import kotlin.math.abs
import kotlin.math.pow

data class DjQuickMode(
    val id: String,
    val emoji: String,
    val name: String,
    val speed: Float,
    val semitones: Int,
    val accentColor: Color,
)

val DjQuickModes = listOf(
    DjQuickMode("original", "🎵", "Original", 1.0f, 0, Color(0xFF00E5FF)),
    DjQuickMode("slowed", "🌙", "Slowed & Reverb", 0.85f, -2, Color(0xFF7C4DFF)),
    DjQuickMode("nightcore", "⚡", "Nightcore", 1.25f, 3, Color(0xFFFF4081)),
    DjQuickMode("vaporwave", "📼", "Vaporwave", 0.75f, -4, Color(0xFF00E676)),
)

@Composable
fun DjQuickBar(
    playerConnection: PlayerConnection,
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    val currentSpeed = playerConnection.player.playbackParameters.speed

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DjQuickModes.forEach { mode ->
            val isActive = abs(currentSpeed - mode.speed) < 0.03f

            val chipBg by animateColorAsState(
                targetValue = if (isActive) mode.accentColor.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                animationSpec = tween(250),
                label = "djChipBg"
            )

            val chipBorder by animateColorAsState(
                targetValue = if (isActive) mode.accentColor.copy(alpha = 0.8f) else Color.White.copy(alpha = 0.15f),
                animationSpec = tween(250),
                label = "djChipBorder"
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(chipBg)
                    .border(1.dp, chipBorder, RoundedCornerShape(12.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        val pitch = 2f.pow(mode.semitones.toFloat() / 12)
                        playerConnection.player.playbackParameters = PlaybackParameters(mode.speed, pitch)
                    }
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = mode.emoji,
                        fontSize = 13.sp
                    )
                    Text(
                        text = mode.name,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
                        ),
                        color = if (isActive) Color.White else Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}
