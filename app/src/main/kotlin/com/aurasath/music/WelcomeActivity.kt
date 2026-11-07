@file:OptIn(ExperimentalTextApi::class)

package com.aurasath.music

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CircularProgressIndicator
import com.aurasath.music.utils.SpotifyLikedSongsImporter
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.SystemUpdate
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.HighQuality
import androidx.compose.material.icons.rounded.Lyrics
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Terminal
import androidx.compose.material.icons.rounded.BlurOn
import androidx.compose.material.icons.rounded.Forum
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.aurasath.music.constants.IsFirstRunKey
import com.aurasath.music.constants.LastSeenChangelogVersionKey
import com.aurasath.music.constants.DataSyncIdKey
import com.aurasath.music.constants.InnerTubeCookieKey
import com.aurasath.music.constants.SpotifySessionKey
import com.aurasath.music.constants.VisitorDataKey
import com.aurasath.music.ui.theme.rememberBrandFontFamily
import com.aurasath.music.ui.theme.rememberGlobalAccentColors
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalView
import android.view.HapticFeedbackConstants
import androidx.compose.material3.TextButton
import com.aurasath.music.viewmodels.SpotifySession
import com.music.innertube.YouTube
import com.music.spotify.Spotify
import com.music.spotify.SpotifyAuth
import com.aurasath.music.ui.theme.vivimusicTheme
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import com.aurasath.music.ui.utils.safeOpenUri
import com.aurasath.music.utils.dataStore
import com.aurasath.music.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.absoluteValue
import androidx.datastore.preferences.core.edit
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature

data class OnboardingPageInfo(
    val content: @Composable (onUpdateScrollState: (Boolean) -> Unit) -> Unit
)

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        runCatching { installSplashScreen() }
        super.onCreate(savedInstanceState)

        val isFirstRun = runCatching { dataStore.get(IsFirstRunKey, true) }.getOrDefault(false)
        val forceShow = intent.getBooleanExtra("FORCE_SHOW", false)

        if (!isFirstRun && !forceShow) {
            finishOnboarding()
            return
        }

        enableEdgeToEdge()
        setContent {
            androidx.compose.runtime.CompositionLocalProvider(
                LocalPlayerAwareWindowInsets provides androidx.compose.foundation.layout.WindowInsets.systemBars
            ) {
                vivimusicTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        var isFinishingOnboarding by remember { mutableStateOf(false) }
                        WelcomePagerScreen(onFinished = {
                            if (isFinishingOnboarding) return@WelcomePagerScreen
                            isFinishingOnboarding = true
                            lifecycleScope.launch {
                                runCatching {
                                    dataStore.edit {
                                        it[IsFirstRunKey] = false
                                        it[LastSeenChangelogVersionKey] = BuildConfig.VERSION_NAME
                                    }
                                }
                                if (forceShow) {
                                    finish()
                                } else {
                                    finishOnboarding()
                                }
                            }
                        })
                    }
                }
            }
        }
    }

    private fun finishOnboarding() {
        runCatching {
            startActivity(Intent(this, MainActivity::class.java))
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
        finish()
    }

}

@OptIn(ExperimentalTextApi::class)
val GoogleSansFlex: FontFamily
    get() = runCatching {
        FontFamily(
            Font(
                resId = com.aurasath.music.R.font.google_sans_flex,
                weight = FontWeight.Normal,
                style = FontStyle.Normal
            )
        )
    }.getOrDefault(FontFamily.Default)

@OptIn(ExperimentalTextApi::class)
@Composable
fun WelcomePagerScreen(onFinished: () -> Unit) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val commonAnimSpec = tween<Float>(durationMillis = 200, easing = FastOutSlowInEasing)
    val commonDpAnimSpec = tween<Dp>(durationMillis = 200, easing = FastOutSlowInEasing)

    val topCardShape =
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    val middleCardShape = RoundedCornerShape(4.dp)
    val bottomCardShape =
        RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 20.dp, bottomEnd = 20.dp)

    val customWelcomeFontFamily = remember {
        runCatching {
            FontFamily(
                Font(
                    resId = com.aurasath.music.R.font.sans_flex,
                    weight = FontWeight.Light
                )
            )
        }.getOrDefault(FontFamily.Default)
    }

    val thinHeaderStyle = TextStyle(
        fontFamily = customWelcomeFontFamily,
        fontSize = 48.sp
    )

    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    var canInstallPackages by remember {
        mutableStateOf(
            if (BuildConfig.FLAVOR.contains("gms") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                runCatching { context.packageManager.canRequestPackageInstalls() }.getOrDefault(false)
            } else true
        )
    }

    var showYtLogin by remember { mutableStateOf(false) }
    var showSpotifyLogin by remember { mutableStateOf(false) }
    var isYtConnected by remember {
        mutableStateOf(context.dataStore.get(InnerTubeCookieKey, "").isNotBlank())
    }
    var isSpotifyConnected by remember {
        mutableStateOf(context.dataStore.get(SpotifySessionKey, "").isNotBlank())
    }
    var isImportingSpotifyLiked by remember { mutableStateOf(false) }
    var spotifyLikedImportDone by remember { mutableStateOf(false) }
    var importedSpotifyLikedCount by remember { mutableStateOf(0) }

    var isLastPageScrolledToEnd by remember { mutableStateOf(true) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    hasNotificationPermission = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    canInstallPackages = runCatching { context.packageManager.canRequestPackageInstalls() }.getOrDefault(false)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )

    val installParamsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canInstallPackages = runCatching { context.packageManager.canRequestPackageInstalls() }.getOrDefault(false)
        }
    }

    val pages = listOf(
        // Page 1: The Grand Hero Welcome
        OnboardingPageInfo(
            content = { _ ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Centerpiece Visual: Pulsing Neon Aurasath Emblem + Equalizer
                    AurasathSonicWaveformVisualizer(
                        modifier = Modifier.size(240.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "AURASATH",
                        fontFamily = rememberBrandFontFamily(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 42.sp,
                        letterSpacing = (-0.5).sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Liquid Glass Music Engine",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Experience ad-free streaming, lossless audio, and synchronized lyrics crafted within a signature frosted glass aesthetic.",
                        fontFamily = GoogleSansFlex,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sleek horizontal feature chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                    ) {
                        OnboardingHighlightBadge(icon = "⚡", label = "Ad-Free")
                        OnboardingHighlightBadge(icon = "🎵", label = "Lossless")
                        OnboardingHighlightBadge(icon = "📝", label = "Lyrics")
                        OnboardingHighlightBadge(icon = "🧊", label = "Liquid Glass")
                    }

                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        ),

        OnboardingPageInfo(
            content = { _ ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    Text(
                        text = "Connect Your",
                        style = thinHeaderStyle,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Music Account",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 56.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Sign in to YouTube Music or Spotify to sync your library, playlists, and recommendations. You can also skip and connect anytime.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = GoogleSansFlex
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        PermissionCard(
                            icon = painterResource(com.aurasath.music.R.drawable.music_note),
                            iconColor = if (isYtConnected) Color(0xFF80da88) else Color(0xFFffb4ab),
                            iconTint = if (isYtConnected) Color(0xFF00522c) else Color(0xFF93000a),
                            title = "YouTube Music",
                            description = if (isYtConnected) "Account connected! Playlists and history will sync automatically." else "Sync your playlists, subscriptions, history & liked songs.",
                            shape = topCardShape,
                            control = {
                                if (isYtConnected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF80da88).copy(alpha = 0.2f),
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF00522c),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Connected",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00522c)
                                            )
                                        }
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                showYtLogin = true
                            }
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        PermissionCard(
                            icon = painterResource(com.aurasath.music.R.drawable.spotify),
                            iconColor = if (isSpotifyConnected) Color(0xFF80da88) else Color(0xFFb2f5ea),
                            iconTint = if (isSpotifyConnected) Color(0xFF00522c) else Color(0xFF005448),
                            title = "Spotify",
                            description = if (isSpotifyConnected) "Connected! Playlists, liked tracks & algorithm active." else "Import playlists, liked tracks & Spotify recommendation engine.",
                            shape = if (isSpotifyConnected) middleCardShape else bottomCardShape,
                            control = {
                                if (isSpotifyConnected) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color(0xFF80da88).copy(alpha = 0.2f),
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Check,
                                                contentDescription = null,
                                                tint = Color(0xFF00522c),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "Connected",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF00522c)
                                            )
                                        }
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Rounded.ChevronRight,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                showSpotifyLogin = true
                            }
                        )

                        if (isSpotifyConnected) {
                            Spacer(modifier = Modifier.height(2.dp))

                            PermissionCard(
                                icon = painterResource(com.aurasath.music.R.drawable.favorite),
                                iconColor = if (spotifyLikedImportDone) Color(0xFF80da88) else Color(0xFFffaee4),
                                iconTint = if (spotifyLikedImportDone) Color(0xFF00522c) else Color(0xFF8d0053),
                                title = "Import Spotify Liked Songs",
                                description = when {
                                    isImportingSpotifyLiked -> "Importing Spotify Liked Songs... ($importedSpotifyLikedCount)"
                                    spotifyLikedImportDone -> "Imported $importedSpotifyLikedCount liked songs into AURASATH library!"
                                    else -> "Sync your Spotify Liked Songs directly into your local library."
                                },
                                shape = bottomCardShape,
                                control = {
                                    if (spotifyLikedImportDone) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFF80da88).copy(alpha = 0.2f),
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Check,
                                                    contentDescription = null,
                                                    tint = Color(0xFF00522c),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Imported",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00522c)
                                                )
                                            }
                                        }
                                    } else if (isImportingSpotifyLiked) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Rounded.CloudDownload,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                },
                                onClick = {
                                    if (!isImportingSpotifyLiked && !spotifyLikedImportDone) {
                                        isImportingSpotifyLiked = true
                                        scope.launch {
                                            val result = SpotifyLikedSongsImporter.importLikedSongs(
                                                context = context,
                                                onProgress = { done, _ -> importedSpotifyLikedCount = done }
                                            )
                                            isImportingSpotifyLiked = false
                                            result.onSuccess { count ->
                                                importedSpotifyLikedCount = count
                                                spotifyLikedImportDone = true
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        ),

        OnboardingPageInfo(
            content = { _ ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(80.dp))

                    Text(
                        text = "Audio &",
                        style = thinHeaderStyle,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Permissions",
                        fontFamily = GoogleSansFlex,
                        fontWeight = FontWeight.Bold,
                        fontSize = 48.sp,
                        color = MaterialTheme.colorScheme.primary,
                        lineHeight = 56.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Enable notifications for lock screen media controls, continuous playback, and background audio optimizations.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = GoogleSansFlex
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        PermissionCard(
                            icon = rememberVectorPainter(Icons.Rounded.Notifications),
                            iconColor = if (hasNotificationPermission) Color(0xFF80da88) else Color(0xFFffaee4),
                            iconTint = if (hasNotificationPermission) Color(0xFF00522c) else Color(0xFF8d0053),
                            title = "Playback Notifications",
                            description = if (hasNotificationPermission) "Notification permission granted! Media controls are active." else "Control playback and view lyrics directly from the notification shade and lock screen.",
                            shape = topCardShape,
                            control = {
                                Switch(
                                    checked = hasNotificationPermission,
                                    onCheckedChange = {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                        } else {
                                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                            }
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                            },
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        PermissionCard(
                            icon = rememberVectorPainter(Icons.Rounded.HighQuality),
                            iconColor = Color(0xFF80da88),
                            iconTint = Color(0xFF00522c),
                            title = "Optimized Background Engine",
                            description = "Lossless caching, gapless transitions, and ultra-low battery drain are pre-configured.",
                            shape = bottomCardShape,
                            control = {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF80da88).copy(alpha = 0.2f),
                                    modifier = Modifier.padding(end = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Check,
                                            contentDescription = null,
                                            tint = Color(0xFF00522c),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Active",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF00522c)
                                        )
                                    }
                                }
                            },
                            onClick = {}
                        )

                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val isFirstPage = pagerState.currentPage == 0
    val isLastPage = pagerState.currentPage == pages.size - 1
    val view = LocalView.current

    val (accentColor) = rememberGlobalAccentColors()
    val ambientGradient = Brush.verticalGradient(
        colors = listOf(
            accentColor.copy(alpha = 0.08f),
            Color.Transparent,
            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f)
        )
    )

    BackHandler(enabled = !isFirstPage) {
        scope.launch {
            pagerState.animateScrollToPage(
                pagerState.currentPage - 1,
                animationSpec = commonAnimSpec
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ambientGradient),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 700.dp)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Top Bar with Skip Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isLastPage) {
                    TextButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onFinished()
                        }
                    ) {
                        Text(
                            text = "Skip",
                            fontFamily = GoogleSansFlex,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset = pagerState.getOffsetDistanceInPages(index)
                            val absOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)
                            alpha = 1f - absOffset
                            translationX = pageOffset * size.width * 0.15f
                            scaleX = 1f - absOffset * 0.05f
                            scaleY = 1f - absOffset * 0.05f
                        }
                ) {
                    pages[index].content { _ -> }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    val selected = index == pagerState.currentPage
                    val dotWidth by animateDpAsState(
                        targetValue = if (selected) 20.dp else 6.dp,
                        animationSpec = commonDpAnimSpec,
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            val backButtonWeight by animateFloatAsState(
                targetValue = if (isFirstPage) 0.0001f else 1f,
                animationSpec = commonAnimSpec,
                label = "backWeight"
            )

            val spacerWeight by animateFloatAsState(
                targetValue = if (isFirstPage) 0.0001f else 0.05f,
                animationSpec = commonAnimSpec,
                label = "spacerWeight"
            )

            val alphaBack by animateFloatAsState(
                targetValue = if (isFirstPage) 0f else 1f,
                animationSpec = commonAnimSpec,
                label = "backAlpha"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .height(64.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(backButtonWeight)
                        .fillMaxHeight()
                        .alpha(alphaBack)
                ) {
                    WelcomeExpressiveButton(
                        text = stringResource(com.aurasath.music.R.string.back_button_desc),
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            if (!isFirstPage) {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage - 1,
                                        animationSpec = commonAnimSpec
                                    )
                                }
                            }
                        },
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxSize(),
                        isOutlined = true
                    )
                }

                Spacer(modifier = Modifier.weight(spacerWeight))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    WelcomeExpressiveButton(
                        text = if (isLastPage) "Start Listening 🚀" else stringResource(
                            com.aurasath.music.R.string.next
                        ),
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            if (isLastPage) {
                                onFinished()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        pagerState.currentPage + 1,
                                        animationSpec = commonAnimSpec
                                    )
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }

        if (showYtLogin) {
            WelcomeYouTubeLoginDialog(
                onDismiss = { showYtLogin = false },
                onLoginSuccess = { cookie, visitorData, dataSyncId ->
                    showYtLogin = false
                    scope.launch(Dispatchers.IO) {
                        context.dataStore.edit { prefs ->
                            prefs[InnerTubeCookieKey] = cookie
                            if (visitorData.isNotBlank()) prefs[VisitorDataKey] = visitorData
                            if (dataSyncId.isNotBlank()) prefs[DataSyncIdKey] = dataSyncId
                        }
                        YouTube.cookie = cookie
                        if (visitorData.isNotBlank()) YouTube.visitorData = visitorData
                        if (dataSyncId.isNotBlank()) YouTube.dataSyncId = dataSyncId.ifBlank { null }
                        isYtConnected = true
                    }
                }
            )
        }

        if (showSpotifyLogin) {
            WelcomeSpotifyLoginDialog(
                onDismiss = { showSpotifyLogin = false },
                onCookiesCaptured = { spDc, spKey ->
                    showSpotifyLogin = false
                    scope.launch(Dispatchers.IO) {
                        runCatching {
                            val token = SpotifyAuth.fetchAccessToken(spDc, spKey).getOrThrow()
                            Spotify.accessToken = token.accessToken
                            val profile = Spotify.me().getOrNull()
                            val newSession = SpotifySession(
                                spDc = spDc,
                                spKey = spKey,
                                accessToken = token.accessToken,
                                expiresAt = token.accessTokenExpirationTimestampMs,
                                accountName = profile?.displayName,
                                accountAvatarUrl = profile?.images?.firstOrNull()?.url
                            )
                            context.dataStore.edit { prefs ->
                                prefs[SpotifySessionKey] = Json { ignoreUnknownKeys = true }.encodeToString(newSession)
                            }
                            isSpotifyConnected = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun AurasathSonicWaveformVisualizer(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "aurasathSonic")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(32000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.2831855f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(170.dp),
            contentAlignment = Alignment.Center
        ) {
            // Concentric ambient sonic rings
            androidx.compose.foundation.Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                        rotationZ = ringRotation
                    }
            ) {
                val maxRadius = size.minDimension / 2f
                drawCircle(
                    color = primaryColor.copy(alpha = 0.15f),
                    radius = maxRadius * 0.95f,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = secondaryColor.copy(alpha = 0.22f),
                    radius = maxRadius * 0.76f,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = tertiaryColor.copy(alpha = 0.30f),
                    radius = maxRadius * 0.58f,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                )
            }

            // Center AURASATH squircle emblem matching splash & about screens
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF0F1015),
                border = BorderStroke(2.dp, primaryColor.copy(alpha = 0.85f)),
                shadowElevation = 18.dp,
                modifier = Modifier.size(92.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = painterResource(com.aurasath.music.R.drawable.aurasath_logo),
                    contentDescription = "AURASATH Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Dancing equalizer bars - 11 smooth animated bars
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(36.dp)
        ) {
            val barCount = 11
            for (i in 0 until barCount) {
                val factor = kotlin.math.sin(wavePhase + i * 0.6f).coerceIn(-1f, 1f)
                val normalized = (factor + 1f) / 2f
                val barHeight = (10 + normalized * 26).dp
                val barColor = if (i % 2 == 0) primaryColor else secondaryColor
                Box(
                    modifier = Modifier
                        .width(4.5.dp)
                        .height(barHeight)
                        .clip(RoundedCornerShape(3.dp))
                        .background(barColor)
                )
            }
        }
    }
}

@Composable
fun OnboardingHighlightBadge(icon: String, label: String) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(text = icon, fontSize = 13.sp)
            Text(
                text = label,
                fontFamily = GoogleSansFlex,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun PermissionCard(
    icon: Painter,
    iconColor: Color,
    iconTint: Color,
    title: String,
    description: String,
    shape: Shape,
    control: @Composable () -> Unit,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressProgress by animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "anim_shape"
    )

    val animatedShape = remember(shape, pressProgress) {
        if (shape is RoundedCornerShape) {
            object : Shape {
                override fun createOutline(
                    size: Size,
                    layoutDirection: LayoutDirection,
                    density: Density
                ): Outline {
                    val targetPx = with(density) { 20.dp.toPx() }
                    fun lerp(start: Float, stop: Float, fraction: Float) =
                        (1 - fraction) * start + fraction * stop

                    val ts = lerp(shape.topStart.toPx(size, density), targetPx, pressProgress)
                    val te = lerp(shape.topEnd.toPx(size, density), targetPx, pressProgress)
                    val bs = lerp(shape.bottomStart.toPx(size, density), targetPx, pressProgress)
                    val be = lerp(shape.bottomEnd.toPx(size, density), targetPx, pressProgress)

                    return Outline.Rounded(
                        androidx.compose.ui.geometry.RoundRect(
                            rect = androidx.compose.ui.geometry.Rect(
                                0f,
                                0f,
                                size.width,
                                size.height
                            ),
                            topLeft = androidx.compose.ui.geometry.CornerRadius(ts),
                            topRight = androidx.compose.ui.geometry.CornerRadius(te),
                            bottomRight = androidx.compose.ui.geometry.CornerRadius(be),
                            bottomLeft = androidx.compose.ui.geometry.CornerRadius(bs)
                        )
                    )
                }
            }
        } else shape
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(animatedShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null, 
                onClick = onClick
            ),
        shape = animatedShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GoogleSansFlex
                )
            },
            supportingContent = {
                Text(
                    text = description,
                    fontFamily = GoogleSansFlex,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconColor,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            trailingContent = control,
            colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun FeatureCard(
    icon: Painter,
    iconColor: Color,
    iconTint: Color,
    title: String,
    description: String,
    shape: Shape
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontFamily = GoogleSansFlex
                )
            },
            supportingContent = {
                Text(
                    text = description,
                    fontFamily = GoogleSansFlex,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            leadingContent = {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = iconColor,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            },
            colors = androidx.compose.material3.ListItemDefaults.colors(containerColor = Color.Transparent)
        )
    }
}

@Composable
fun WelcomeExpressiveButton(
    text: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    isOutlined: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        label = "buttonScale"
    )

    Surface(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .then(if (isPressed) Modifier.rotate(0f) else Modifier), // placeholder for scale
        color = containerColor,
        contentColor = contentColor,
        border = if (isOutlined) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null,
        shape = CircleShape,
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontFamily = GoogleSansFlex,
                fontSize = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeYouTubeLoginDialog(
    onDismiss: () -> Unit,
    onLoginSuccess: (cookie: String, visitorData: String, dataSyncId: String) -> Unit,
) {
    val context = LocalContext.current
    var webView by remember { mutableStateOf<WebView?>(null) }
    var pageProgress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var hasCompletedLogin by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val liveVisitorData = remember { AtomicReference("") }
    val liveDataSyncId = remember { AtomicReference("") }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.loadUrl("about:blank")
            webView?.destroy()
            webView = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopAppBar(
                        title = { Text("Log in to YouTube Music") },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    painterResource(com.aurasath.music.R.drawable.close),
                                    contentDescription = "Close"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { webView?.reload() }) {
                                Icon(
                                    painterResource(com.aurasath.music.R.drawable.sync),
                                    contentDescription = "Reload"
                                )
                            }
                        },
                        windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    )
                    if (isLoading || pageProgress in 1..99) {
                        LinearProgressIndicator(
                            progress = { pageProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { webViewContext ->
                        try {
                            WebView(webViewContext).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false
                                    userAgentString = userAgentString.replace("; wv", "")
                                }
                                if (WebViewFeature.isFeatureSupported(WebViewFeature.REQUESTED_WITH_HEADER_ALLOW_LIST)) {
                                    WebSettingsCompat.setRequestedWithHeaderOriginAllowList(settings, emptySet())
                                }
                                addJavascriptInterface(object {
                                @JavascriptInterface
                                fun onRetrieveVisitorData(newVisitorData: String?) {
                                    if (!newVisitorData.isNullOrBlank() && newVisitorData != "null") {
                                        val decoded = android.net.Uri.decode(newVisitorData)
                                        liveVisitorData.set(decoded)
                                    }
                                }
                                @JavascriptInterface
                                fun onRetrieveDataSyncId(newDataSyncId: String?) {
                                    if (!newDataSyncId.isNullOrBlank() && newDataSyncId != "null") {
                                        liveDataSyncId.set(newDataSyncId)
                                    }
                                }
                            }, "Android")

                            webChromeClient = object : WebChromeClient() {
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    pageProgress = newProgress
                                    if (newProgress >= 100) isLoading = false
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    isLoading = true
                                }
                                override fun onPageFinished(view: WebView, url: String?) {
                                    isLoading = false
                                    loadUrl("javascript:Android.onRetrieveVisitorData(window.yt.config_.VISITOR_DATA)")
                                    loadUrl("javascript:Android.onRetrieveDataSyncId(window.yt.config_.DATASYNC_ID)")

                                    if (url?.startsWith("https://music.youtube.com") == true && !hasCompletedLogin) {
                                        val cookie = CookieManager.getInstance().getCookie(url)
                                        if (cookie.isNullOrBlank()) return
                                        hasCompletedLogin = true
                                        coroutineScope.launch {
                                            var waitedMs = 0
                                            while (liveVisitorData.get().isBlank() && waitedMs < 5000) {
                                                delay(100)
                                                waitedMs += 100
                                            }
                                            val newVisitorData = liveVisitorData.get()
                                            val syncId = liveDataSyncId.get()
                                            onLoginSuccess(cookie, newVisitorData, syncId)
                                        }
                                    }
                                }
                            }
                            webView = this
                            loadUrl("https://accounts.google.com/ServiceLogin?continue=https%3A%2F%2Fmusic.youtube.com")
                        }
                    } catch (e: Throwable) {
                        WebView(webViewContext).apply {
                            webView = this
                        }
                    }
                },
                update = { view -> webView = view }
                )
            }
        }
    }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeSpotifyLoginDialog(
    onDismiss: () -> Unit,
    onCookiesCaptured: (spDc: String, spKey: String) -> Unit,
) {
    var webView by remember { mutableStateOf<WebView?>(null) }
    var captured by remember { mutableStateOf(false) }
    var pageProgress by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        onDispose {
            webView?.stopLoading()
            webView?.loadUrl("about:blank")
            webView?.destroy()
            webView = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TopAppBar(
                        title = { Text(stringResource(com.aurasath.music.R.string.spotify_login_title)) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(
                                    painterResource(com.aurasath.music.R.drawable.close),
                                    contentDescription = "Close"
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = { webView?.reload() }) {
                                Icon(
                                    painterResource(com.aurasath.music.R.drawable.sync),
                                    contentDescription = "Reload"
                                )
                            }
                        },
                        windowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    )
                    if (isLoading || pageProgress in 1..99) {
                        LinearProgressIndicator(
                            progress = { pageProgress / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        try {
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)

                                val cookieManager = CookieManager.getInstance()
                                cookieManager.setAcceptCookie(true)
                                cookieManager.setAcceptThirdPartyCookies(this, true)

                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    setSupportZoom(true)
                                    builtInZoomControls = true
                                    displayZoomControls = false

                                    val rawUa = userAgentString.orEmpty()
                                    userAgentString = if (rawUa.contains("; wv")) {
                                        rawUa.replace("; wv", "")
                                    } else if (rawUa.isNotBlank()) {
                                        rawUa
                                    } else {
                                        "Mozilla/5.0 (Linux; Android 14; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Mobile Safari/537.36"
                                    }
                                }

                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        pageProgress = newProgress
                                        if (newProgress >= 100) {
                                            isLoading = false
                                        }
                                    }
                                }

                                webViewClient = object : WebViewClient() {
                                    private fun captureCookies(url: String?): Boolean {
                                        if (captured) return true
                                        cookieManager.flush()
                                        val allCookies = buildString {
                                            append(cookieManager.getCookie("https://open.spotify.com") ?: "")
                                            append(";")
                                            append(cookieManager.getCookie("https://accounts.spotify.com") ?: "")
                                            append(";")
                                            append(cookieManager.getCookie("https://spotify.com") ?: "")
                                            if (url != null) {
                                                append(";")
                                                append(cookieManager.getCookie(url) ?: "")
                                            }
                                        }
                                        val cookies = allCookies.split(";").associate {
                                            val parts = it.split("=")
                                            val key = parts.firstOrNull()?.trim().orEmpty()
                                            val valStr = parts.drop(1).joinToString("=").trim()
                                            key to valStr
                                        }
                                        val spDc = cookies["sp_dc"].orEmpty()
                                        if (spDc.isBlank()) return false
                                        captured = true
                                        val spKey = cookies["sp_key"].orEmpty()
                                        onCookiesCaptured(spDc, spKey)
                                        return true
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView,
                                        request: WebResourceRequest,
                                    ): Boolean = captureCookies(request.url?.toString())

                                    override fun onPageStarted(
                                        view: WebView,
                                        url: String?,
                                        favicon: Bitmap?,
                                    ) {
                                        isLoading = true
                                        captureCookies(url)
                                    }

                                    override fun onPageFinished(view: WebView, url: String?) {
                                        isLoading = false
                                        captureCookies(url)
                                    }
                                }

                                webView = this
                                cookieManager.removeAllCookies(null)
                                cookieManager.flush()
                                loadUrl(SpotifyAuth.LOGIN_URL)
                            }
                        } catch (e: Throwable) {
                            WebView(context).apply {
                                webView = this
                            }
                        }
                    },
                    update = { view ->
                        webView = view
                    }
                )
            }
        }
    }

    BackHandler(enabled = webView?.canGoBack() == true) {
        webView?.goBack()
    }
}
