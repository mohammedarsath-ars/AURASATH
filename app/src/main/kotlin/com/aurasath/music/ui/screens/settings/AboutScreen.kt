/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.screens.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.aurasath.music.ui.utils.bounceClick
import com.aurasath.music.ui.utils.combinedBounceClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.aurasath.music.ui.theme.rememberBrandFontFamily
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
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
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import com.aurasath.music.ui.utils.backToMain
import com.aurasath.music.ui.utils.safeOpenUri
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AboutScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    onBack: (() -> Unit)? = null,
) {
    val uriHandler = LocalUriHandler.current
    val context = LocalContext.current
    val unknownString = stringResource(R.string.unknown)

    val cookieShape = MaterialShapes.Cookie7Sided.toShape()
    
    val installedDate = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val installTime = packageInfo.firstInstallTime
            SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(installTime))
        } catch (_: Exception) {
            unknownString
        }
    }

    Column(
        Modifier
            .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        // Header with Official AURASATH Logo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // AURASATH Logo with glowing ambient backdrop and elevated container
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 18.dp)
            ) {
                // Ambient glow
                Box(
                    modifier = Modifier
                        .size(104.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.32f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
                // Elevated Logo Container
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = Color.Transparent,
                    border = BorderStroke(
                        1.5.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    shadowElevation = 10.dp,
                    modifier = Modifier.size(88.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.aurasath_logo),
                        contentDescription = stringResource(R.string.wrapped_logo_content_description),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(22.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Text(
                text = "AURASATH",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = rememberBrandFontFamily(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 38.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .border(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.aurasath_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "v${BuildConfig.VERSION_NAME} • ${stringResource(if (BuildConfig.IS_NIGHTLY) R.string.build_nightly else R.string.build_stable)}",
                    style = MaterialTheme.typography.titleMedium,
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
                )
            )
        )
        Spacer(modifier = Modifier.height(27.dp))

        // Community Section
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
                    icon = painterResource(R.drawable.discord),
                    title = { Text(stringResource(R.string.discord_channel)) },
                    description = { Text(stringResource(R.string.join_discord)) },
                    onClick = { uriHandler.safeOpenUri(context, "https://discord.gg/spbuDTePSR") }
                )
            )
        )

        Spacer(modifier = Modifier.height(27.dp))


        // App Information Section
        Material3SettingsGroup(
            title = stringResource(R.string.app_info_section),
            items = listOf(
                Material3SettingsItem(
                    icon = painterResource(R.drawable.deployed_app_update),
                    title = { Text(stringResource(R.string.installed_date_title)) },
                    description = { Text(installedDate) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.info),
                    title = { Text(stringResource(R.string.version_code)) },
                    description = { Text(BuildConfig.VERSION_CODE.toString()) }
                ),
                Material3SettingsItem(
                    icon = painterResource(R.drawable.license_vivi),
                    title = { Text(stringResource(R.string.license)) },
                    description = { Text("GPL-3.0 • Free Open Source Software") },
                    onClick = { uriHandler.safeOpenUri(context, "https://github.com/mohammedarsath-ars/AURASATH/blob/main/LICENSE") }
                ),
            )
        )
        Spacer(modifier = Modifier.height(20.dp))

        Image(
            painter = painterResource(R.drawable.make_in_india),
            contentDescription = "Make in India",
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(vertical = 16.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(20.dp))
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