package com.aurasath.music.utils

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

object AppIconManager {
    const val ALIAS_GLASS = "com.aurasath.music.LauncherGlass"
    const val ALIAS_LAVENDER = "com.aurasath.music.LauncherLavender"
    const val ALIAS_SKY = "com.aurasath.music.LauncherSky"
    const val ALIAS_SAGE = "com.aurasath.music.LauncherSage"
    const val ALIAS_ROSE = "com.aurasath.music.LauncherRose"
    const val ALIAS_LIGHT = "com.aurasath.music.LauncherLight"

    val ALL_ALIASES = listOf(
        ALIAS_GLASS,
        ALIAS_LAVENDER,
        ALIAS_SKY,
        ALIAS_SAGE,
        ALIAS_ROSE,
        ALIAS_LIGHT
    )

    /**
     * Enables [targetAlias] and disables all others using [PackageManager.DONT_KILL_APP].
     */
    fun setLauncherIcon(context: Context, targetAlias: String) {
        val pm = context.packageManager
        val pkg = context.packageName

        try {
            for (alias in ALL_ALIASES) {
                val component = ComponentName(pkg, alias)
                val isTarget = alias == targetAlias
                val desiredState = if (isTarget) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                } else {
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                }

                val currentState = pm.getComponentEnabledSetting(component)
                if (currentState != desiredState) {
                    pm.setComponentEnabledSetting(
                        component,
                        desiredState,
                        PackageManager.DONT_KILL_APP
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Chooses the best matching Adobe "Pw" launcher icon for the current UI color and Day/Night mode.
     */
    fun syncWithUiTheme(context: Context, themeColor: Color, isDark: Boolean) {
        val targetAlias = if (!isDark) {
            ALIAS_LIGHT
        } else {
            val argb = themeColor.toArgb()
            val r = (argb shr 16) and 0xFF
            val g = (argb shr 8) and 0xFF
            val b = argb and 0xFF

            when {
                // Sky / Cyan (high blue and green)
                b > 180 && g > 150 -> ALIAS_SKY
                // Sage / Green (green dominates)
                g > r && g > b -> ALIAS_SAGE
                // Rose / Pink / Amber (high red, moderate green/blue)
                r > 200 && (g > 100 || b > 140) -> ALIAS_ROSE
                // Default electric violet / lavender
                else -> ALIAS_LAVENDER
            }
        }

        setLauncherIcon(context, targetAlias)
    }
}
