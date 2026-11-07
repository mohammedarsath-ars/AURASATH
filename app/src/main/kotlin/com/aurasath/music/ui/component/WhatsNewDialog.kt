/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.component

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aurasath.music.BuildConfig
import com.aurasath.music.R
import com.aurasath.music.constants.LastSeenChangelogVersionKey
import com.aurasath.music.ui.theme.AppleTokens
import com.aurasath.music.utils.rememberPreference

/**
 * Automatically prompts the user with a "What's New" changelog summary
 * when they install AURASATH or update to a new version.
 */
@Composable
fun WhatsNewPromptHost(
    navController: NavController? = null
) {
    var lastSeenVersion by rememberPreference(LastSeenChangelogVersionKey, "")
    val currentVersion = BuildConfig.VERSION_NAME

    var visible by remember(lastSeenVersion, currentVersion) {
        // Do not prompt on brand new installs (user just went through onboarding);
        // only show when upgrading from a previously installed version.
        mutableStateOf(lastSeenVersion.isNotBlank() && lastSeenVersion != currentVersion)
    }

    if (!visible) return

    fun dismiss() {
        visible = false
        lastSeenVersion = currentVersion
    }

    AlertDialog(
        onDismissRequest = ::dismiss,
        shape = RoundedCornerShape(AppleTokens.CardCornerLarge),
        icon = {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.update),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "What's New in AURASATH",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Version $currentVersion Release Notes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ChangelogHighlightItem(
                    emoji = "🌙",
                    title = "Smart Sleep Fade",
                    description = "Sleep timer gently fades volume down to silence over the final 10 seconds before pausing playback."
                )

                ChangelogHighlightItem(
                    emoji = "⚡",
                    title = "120fps Silky Tab Crossfades",
                    description = "Ultra-smooth, zero-stutter hardware-accelerated transitions between Home, Library, and Settings."
                )

                ChangelogHighlightItem(
                    emoji = "👆",
                    title = "Swipe-to-Play-Next",
                    description = "Swipe right on any song in playlists or library to instantly queue it next with haptic confirmation."
                )

                ChangelogHighlightItem(
                    emoji = "🎵",
                    title = "Spotify & YouTube Music Sync",
                    description = "Dual account connection, Spotify Liked Songs importer, and dual Wrapped playback synchronization."
                )

                ChangelogHighlightItem(
                    emoji = "🧊",
                    title = "Liquid Glass & Foldable Split-View",
                    description = "Refined frosted glass styling with automatic dual-pane player layout on foldables and tablets."
                )

                ChangelogHighlightItem(
                    emoji = "📡",
                    title = "Google Cast & Lossless Audio",
                    description = "One-tap Chromecast streaming, gapless playback, and high-fidelity YouTube Music streaming."
                )
            }
        },
        confirmButton = {
            Button(
                onClick = ::dismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Let's Explore!")
            }
        },
        dismissButton = {
            if (navController != null) {
                TextButton(
                    onClick = {
                        dismiss()
                        navController.navigate("settings/changelog")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Full Changelog")
                }
            }
        }
    )
}

@Composable
private fun ChangelogHighlightItem(
    emoji: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}
