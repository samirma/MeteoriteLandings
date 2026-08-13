package com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext

/**
 * Whether the app is currently rendering its dark theme.
 *
 * This replaces `ExtendedTheme.isLight`, a process-wide `var` that the theme wrote to *during
 * composition* — not restartable, not thread-safe under parallel composition, and invisible to
 * the snapshot system, so readers never recomposed when it changed.
 */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

/**
 * @param dynamicColor opts into Material You wallpaper colours (Android 12+). Off by default:
 *   the app's illustrations and vector drawables are drawn in a fixed brand blue, so a wallpaper
 *   palette would sit next to artwork it does not match.
 */
@Composable
fun MeteoriteLandingsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalIsDarkTheme provides darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            shapes = Shapes,
            typography = MeteoriteTypography,
            content = content,
        )
    }
}

object MeteoriteTheme {
    val isDark: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsDarkTheme.current
}
