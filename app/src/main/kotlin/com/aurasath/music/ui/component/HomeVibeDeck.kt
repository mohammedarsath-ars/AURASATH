/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurasath.music.ui.theme.rememberBrandFontFamily

data class VibeItem(
    val id: String,
    val icon: String,
    val title: String,
    val query: String,
    val gradientColors: List<Color>,
)

val DefaultVibes = listOf(
    VibeItem(
        id = "workout",
        icon = "⚡",
        title = "Workout",
        query = "high energy workout edm phonk gym mix",
        gradientColors = listOf(Color(0xFFFF3D00), Color(0xFFFF9100))
    ),
    VibeItem(
        id = "midnight",
        icon = "🌙",
        title = "Midnight Lofi",
        query = "midnight lofi chill hip hop beats",
        gradientColors = listOf(Color(0xFF651FFF), Color(0xFF00E5FF))
    ),
    VibeItem(
        id = "chill",
        icon = "☕",
        title = "Acoustic Chill",
        query = "acoustic chill relax coffeehouse",
        gradientColors = listOf(Color(0xFF00B0FF), Color(0xFF00E676))
    ),
    VibeItem(
        id = "drive",
        icon = "🚗",
        title = "Night Drive",
        query = "night drive synthwave phonk dark retro",
        gradientColors = listOf(Color(0xFFD500F9), Color(0xFF2979FF))
    ),
    VibeItem(
        id = "romance",
        icon = "❤️",
        title = "Romance",
        query = "romantic love songs acoustic melodies",
        gradientColors = listOf(Color(0xFFFF1744), Color(0xFFFF80AB))
    ),
    VibeItem(
        id = "focus",
        icon = "🚀",
        title = "Deep Focus",
        query = "deep focus ambient instrumental study",
        gradientColors = listOf(Color(0xFF00E5FF), Color(0xFF1DE9B6))
    )
)

/**
 * AURASATH 1-Tap Vibe Deck.
 * Floating frosted glass chips for instant mood radio streaming.
 */
@Composable
fun HomeVibeDeck(
    onVibeSelected: (VibeItem) -> Unit,
    modifier: Modifier = Modifier,
    activeVibeId: String? = null,
) {
    val haptic = LocalHapticFeedback.current
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DefaultVibes.forEach { vibe ->
            val isSelected = activeVibeId == vibe.id

            val borderGlow by animateColorAsState(
                targetValue = if (isSelected) vibe.gradientColors.first() else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f),
                animationSpec = tween(300),
                label = "vibeBorder"
            )

            val bgAlpha by animateColorAsState(
                targetValue = if (isSelected) vibe.gradientColors.first().copy(alpha = 0.28f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                animationSpec = tween(300),
                label = "vibeBg"
            )

            val elevation by animateDpAsState(
                targetValue = if (isSelected) 6.dp else 0.dp,
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                label = "vibeElevation"
            )

            Box(
                modifier = Modifier
                    .shadow(elevation, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(bgAlpha)
                    .border(1.2.dp, borderGlow, RoundedCornerShape(16.dp))
                    .clickable {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        onVibeSelected(vibe)
                    }
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = vibe.icon,
                        fontSize = 15.sp
                    )
                    Text(
                        text = vibe.title,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            fontFamily = rememberBrandFontFamily()
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

