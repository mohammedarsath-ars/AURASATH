/**
 * AURASATH Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.aurasath.music.playback.queues

import androidx.media3.common.MediaItem
import com.aurasath.music.extensions.metadata
import com.aurasath.music.models.MediaMetadata

interface Queue {
    val preloadItem: MediaMetadata?

    suspend fun getInitialStatus(): Status

    fun hasNextPage(): Boolean

    suspend fun nextPage(): List<MediaItem>

    data class Status(
        val title: String?,
        val items: List<MediaItem>,
        val mediaItemIndex: Int,
        val position: Long = 0L,
    ) {
        fun filterExplicit(enabled: Boolean = true): Status {
            if (!enabled) return this
            val currentMediaId = items.getOrNull(mediaItemIndex)?.mediaId
            val filtered = items.filterExplicit()
            val newIndex = if (currentMediaId != null) {
                val found = filtered.indexOfFirst { it.mediaId == currentMediaId }
                if (found != -1) found else mediaItemIndex.coerceIn(0, (filtered.size - 1).coerceAtLeast(0))
            } else {
                mediaItemIndex.coerceIn(0, (filtered.size - 1).coerceAtLeast(0))
            }
            return copy(items = filtered, mediaItemIndex = newIndex)
        }

        fun filterVideoSongs(disableVideos: Boolean = false): Status {
            if (!disableVideos) return this
            val currentMediaId = items.getOrNull(mediaItemIndex)?.mediaId
            val filtered = items.filterVideoSongs(true)
            val newIndex = if (currentMediaId != null) {
                val found = filtered.indexOfFirst { it.mediaId == currentMediaId }
                if (found != -1) found else mediaItemIndex.coerceIn(0, (filtered.size - 1).coerceAtLeast(0))
            } else {
                mediaItemIndex.coerceIn(0, (filtered.size - 1).coerceAtLeast(0))
            }
            return copy(items = filtered, mediaItemIndex = newIndex)
        }
    }
}

fun List<MediaItem>.filterExplicit(enabled: Boolean = true) =
    if (enabled) {
        filterNot {
            it.metadata?.explicit == true
        }
    } else {
        this
    }

fun List<MediaItem>.filterVideoSongs(disableVideos: Boolean = false) =
    if (disableVideos) {
        filterNot { it.metadata?.isVideoSong == true }
    } else {
        this
    }
