/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.screens.settings

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    val cookieShape = MaterialShapes.Cookie7Sided.toShape()

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        // Header with Official AURASATH Logo & Glow
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AURASATH Logo with glowing ambient backdrop and elevated container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Ambient glow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Elevated Logo Container
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.5.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    shadowElevation = 12.dp,
                    modifier = Modifier.size(92.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.aurasath_logo),
                        contentDescription = stringResource(R.string.wrapped_logo_content_description),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(24.dp)),
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

            // Version Badge with live indicator dot
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

        // Developer Section
        Material3SettingsGroup(
            title = stringResource(R.string.developer_section),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.dev),
                    title = { Text(stringResource(R.string.developer_name)) },
                    description = { Text(stringResource(R.string.app_developer), color = MaterialTheme.colorScheme.primary) },
                    tintIcon = false,
                    iconShape = cookieShape,
                    onClick = { uriHandler.safeOpenUri(context, "https://github.com/mohammedarsath-ars") }
                ),
                Material3SettingsItem(
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF833AB4),
                                            Color(0xFFFD1D1D),
                                            Color(0xFFFCB045)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.instagram),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    },
                    title = { Text("Instagram") },
                    description = { Text("@freaky____arsu", color = MaterialTheme.colorScheme.primary) },
                    onClick = { uriHandler.safeOpenUri(context, "https://www.instagram.com/freaky____arsu") }
                )
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

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
                    description = { Text("Report bugs or request new features") },
                    onClick = { navController.navigate("settings/suggestions") }
                )
            )
        )
        Spacer(modifier = Modifier.height(24.dp))

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
        Spacer(modifier = Modifier.height(32.dp))

        // Minimal Aesthetic Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crafted with ❤️ by Arsath Arsu",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
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
