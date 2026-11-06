/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.screens.settings

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aurasath.music.BuildConfig
import com.aurasath.music.LocalPlayerAwareWindowInsets
import com.aurasath.music.R
import com.aurasath.music.ui.component.IconButton
import com.aurasath.music.ui.component.Material3SettingsGroup
import com.aurasath.music.ui.component.Material3SettingsItem
import com.aurasath.music.ui.theme.rememberBrandFontFamily
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import com.aurasath.music.ui.utils.backToMain
import com.aurasath.music.ui.utils.bounceClick
import com.aurasath.music.ui.utils.safeOpenUri

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: (() -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current

    // Luxury Breathing Aura Animation
    val infiniteTransition = rememberInfiniteTransition(label = "AuraPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        // Hero Header with Living Aura Glow
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Breathing Dynamic Ambient Halo
                Box(
                    modifier = Modifier
                        .size(116.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                            alpha = pulseAlpha
                        }
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                                    Color(0xFF833AB4).copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Elevated Squircle Container
                Surface(
                    shape = RoundedCornerShape(26.dp),
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                Color(0xFF833AB4).copy(alpha = 0.6f)
                            )
                        )
                    ),
                    shadowElevation = 14.dp,
                    modifier = Modifier.size(94.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.aurasath_logo),
                        contentDescription = stringResource(R.string.wrapped_logo_content_description),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(26.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(
                text = "AURASATH",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = rememberBrandFontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Next-Gen Open-Source Music Experience",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Version Pill with Live Indicator
            Row(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        shape = CircleShape
                    )
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color(0xFF34C759), CircleShape)
                )

                Text(
                    text = "v${BuildConfig.VERSION_NAME} • ${stringResource(if (BuildConfig.IS_NIGHTLY) R.string.build_nightly else R.string.build_stable)}",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 3 Flagship Highlights Cards
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val highlights = listOf(
                Triple("100% Ad-Free", "Pure Sound", Color(0xFF34C759)),
                Triple("Lossless", "Hi-Res Audio", Color(0xFF007AFF)),
                Triple("Open Source", "GPL-3.0", Color(0xFFFF9500))
            )
            highlights.forEach { (title, subtitle, accentColor) ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                    border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.35f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(accentColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // VIP Creator Showcase Card
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        Color(0xFF833AB4).copy(alpha = 0.45f),
                        Color(0xFFFD1D1D).copy(alpha = 0.3f),
                        Color(0xFF007AFF).copy(alpha = 0.4f)
                    )
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar with glowing gradient ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                Brush.sweepGradient(
                                    listOf(
                                        Color(0xFF833AB4),
                                        Color(0xFFFD1D1D),
                                        Color(0xFFFCB045),
                                        Color(0xFF007AFF),
                                        Color(0xFF833AB4)
                                    )
                                ),
                                shape = CircleShape
                            )
                            .padding(2.5.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.dev),
                            contentDescription = "Arsath Arsu",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Arsath Arsu",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            // Blue Verified Badge
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color(0xFF007AFF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.check),
                                    contentDescription = "Verified",
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = "Creator & Lead Architect",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Social Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Instagram Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .bounceClick {
                                uriHandler.safeOpenUri(context, "https://www.instagram.com/freaky____arsu")
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFF833AB4),
                                            Color(0xFFFD1D1D),
                                            Color(0xFFFCB045)
                                        )
                                    )
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.instagram),
                                    contentDescription = "Instagram",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Instagram",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // GitHub Button
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1E2228),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                        modifier = Modifier
                            .weight(1f)
                            .bounceClick {
                                uriHandler.safeOpenUri(context, "https://github.com/mohammedarsath-ars")
                            }
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.github),
                                    contentDescription = "GitHub",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "GitHub",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Community & Project Section
        Material3SettingsGroup(
            title = stringResource(R.string.community_section),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.github),
                    title = { Text(stringResource(R.string.github_repository)) },
                    description = { Text(stringResource(R.string.view_source_code)) },
                    onClick = { uriHandler.safeOpenUri(context, "https://github.com/mohammedarsath-ars/AURASATH") }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.edit),
                    title = { Text("Suggestions & Feedback") },
                    description = { Text("Report bugs or request new features directly to Admin") },
                    onClick = { navController.navigate("settings/suggestions") }
                )
            )
        )
        Spacer(modifier = Modifier.height(18.dp))

        // App Information / License Section
        Material3SettingsGroup(
            title = stringResource(R.string.app_info_section),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.license_vivi),
                    title = { Text(stringResource(R.string.license)) },
                    description = { Text("GPL-3.0 • Free Open Source Software") },
                    onClick = { uriHandler.safeOpenUri(context, "https://github.com/mohammedarsath-ars/AURASATH/blob/main/LICENSE") }
                ),
            )
        )
        Spacer(modifier = Modifier.height(30.dp))

        // Ultimate Signature Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crafted with ❤️ in India by Arsath Arsu",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "AURASATH • All Rights Reserved",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }

    TopAppBar(
        windowInsets = appTopBarWindowInsets(),
        title = { Text(stringResource(R.string.about)) },
        navigationIcon = {
            IconButton(
                onClick = { onBack?.invoke() ?: navController.navigateUp() },
                onLongClick = navController::backToMain,
            ) {
                Icon(
                    painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        },
        scrollBehavior = scrollBehavior
    )
}
