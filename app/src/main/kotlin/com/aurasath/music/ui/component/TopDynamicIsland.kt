/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.aurasath.music.models.MediaMetadata
import com.aurasath.music.ui.screens.AnimatedEqualizerIndicator
import com.aurasath.music.ui.theme.rememberBrandFontFamily

/**
 * AURASATH Top Floating Dynamic Island.
 * Compact cosmic glass pill at top of screen displaying active playback.
 */
@Composable
fun TopDynamicIsland(
    mediaMetadata: MediaMetadata?,
    isPlaying: Boolean,
    visible: Boolean,
    onClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible && mediaMetadata != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(stiffness = 400f, dampingRatio = 0.82f)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = spring(stiffness = 500f)
        ) + fadeOut(),
        modifier = modifier
    ) {
        if (mediaMetadata == null) return@AnimatedVisibility

        val primaryColor = MaterialTheme.colorScheme.primary

        Box(
            modifier = Modifier
                .padding(top = 10.dp)
                .shadow(16.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF141722).copy(alpha = 0.94f),
                            Color(0xFF090A10).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.horizontalGradient(
                        listOf(
                            primaryColor.copy(alpha = 0.65f),
                            Color(0xFF00E5FF).copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .clickable(onClick = onClick)
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .widthIn(min = 160.dp, max = 280.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Mini Artwork
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black)
                ) {
                    AsyncImage(
                        model = mediaMetadata.thumbnailUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Track Info
                Text(
                    text = mediaMetadata.title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = rememberBrandFontFamily(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Mini Equalizer Bars
                AnimatedEqualizerIndicator(
                    color = primaryColor,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(Modifier.width(2.dp))

                // Play/Pause Micro Button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f))
                        .clickable(onClick = onPlayPauseClick)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = primaryColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
