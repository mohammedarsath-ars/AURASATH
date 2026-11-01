/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.ui.screens.artist

import com.aurasath.music.ui.utils.appTopBarWindowInsets
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.activity.compose.BackHandler
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.ExperimentalFoundationApi
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Box
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Row
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.Spacer
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.padding
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.GridCells
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.GridItemSpan
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.items
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Icon
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.MaterialTheme
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SnackbarHost
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.SnackbarHostState
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.Text
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBar
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.material3.TopAppBarScrollBehavior
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.Composable
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.collectAsState
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.getValue
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateListOf
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.mutableStateOf
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.remember
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.rememberCoroutineScope
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.listSaver
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.saveable.rememberSaveable
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.setValue
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.runtime.toMutableStateList
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Alignment
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.Modifier
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.painterResource
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.res.pluralStringResource
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.compose.ui.unit.dp
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import androidx.navigation.NavController
import com.aurasath.music.LocalPlayerAwareWindowInsets
import com.aurasath.music.LocalPlayerConnection
import com.aurasath.music.R
import com.aurasath.music.constants.CONTENT_TYPE_ALBUM
import com.aurasath.music.constants.CONTENT_TYPE_HEADER
import com.aurasath.music.constants.GridItemSize
import com.aurasath.music.constants.GridItemsSizeKey
import com.aurasath.music.constants.GridThumbnailHeight
import com.aurasath.music.ui.component.IconButton
import com.aurasath.music.ui.component.LibraryAlbumGridItem
import com.aurasath.music.ui.component.ListScrollRail
import com.aurasath.music.ui.component.LocalMenuState
import com.aurasath.music.ui.utils.appTopBarWindowInsets
import com.aurasath.music.ui.utils.backToMain
import com.aurasath.music.ui.utils.rememberGridColumns
import com.aurasath.music.utils.rememberEnumPreference
import com.aurasath.music.viewmodels.ArtistAlbumsViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ArtistAlbumsScreen(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    viewModel: ArtistAlbumsViewModel = hiltViewModel(),
) {
    val menuState = LocalMenuState.current
    val playerConnection = LocalPlayerConnection.current ?: return
    val isPlaying by playerConnection.isEffectivelyPlaying.collectAsState()
    val mediaMetadata by playerConnection.mediaMetadata.collectAsState()

    val artist by viewModel.artist.collectAsState()
    val albums by viewModel.albums.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val lazyGridState = rememberLazyGridState()
    val gridItemSize by rememberEnumPreference(GridItemsSizeKey, GridItemSize.BIG)

    var inSelectMode by rememberSaveable { mutableStateOf(false) }
    val selection = rememberSaveable(
        saver = listSaver<MutableList<String>, String>(
            save = { it.toList() },
            restore = { it.toMutableStateList() }
        )
    ) { mutableStateListOf() }
    val onExitSelectionMode = {
        inSelectMode = false
        selection.clear()
    }
    if (inSelectMode) {
        BackHandler(onBack = onExitSelectionMode)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    val visibleAlbums = remember(albums) { albums.distinctBy { it.id } }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            state = lazyGridState,
            columns = rememberGridColumns(),
            contentPadding = LocalPlayerAwareWindowInsets.current.asPaddingValues()
        ) {
            item(
                key = "header",
                span = { GridItemSpan(maxLineSpan) },
                contentType = CONTENT_TYPE_HEADER
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Spacer(Modifier.weight(1f))

                    Text(
                        text = pluralStringResource(R.plurals.n_album, albums.size, albums.size),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            items(
                items = visibleAlbums,
                key = { it.id },
                contentType = { CONTENT_TYPE_ALBUM }
            ) { album ->
                LibraryAlbumGridItem(
                    navController = navController,
                    menuState = menuState,
                    coroutineScope = coroutineScope,
                    album = album,
                    isActive = album.id == mediaMetadata?.album?.id,
                    isPlaying = isPlaying,
                    modifier = Modifier.animateItem()
                )
            }
        }

        // No sort control on this screen -- albums arrive in release order -- so the rail
        // is a proportional thumb rather than letters.
        ListScrollRail(
            lazyGridState = lazyGridState,
            itemCount = visibleAlbums.size,
            sectionIndexMap = null,
        )

        TopAppBar(
            windowInsets = appTopBarWindowInsets(),
            title = { Text(artist?.artist?.name.orEmpty()) },
            navigationIcon = {
                IconButton(
                    onClick = navController::navigateUp,
                    onLongClick = navController::backToMain
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null
                    )
                }
            },
            scrollBehavior = scrollBehavior
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .windowInsetsPadding(LocalPlayerAwareWindowInsets.current)
                .align(Alignment.BottomCenter)
        )
    }
}
