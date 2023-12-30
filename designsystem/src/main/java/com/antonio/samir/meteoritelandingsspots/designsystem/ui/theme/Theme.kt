package com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val textPrimary: Color,
    val textSecondary: Color,
    val highlight: Color,
    val header: Color,
    val hover: Color,
    val textPrimaryInverted: Color,
    val textSecondaryInverted: Color,
    val backgroundInverted: Color,
    val divider: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        highlight = Color.Unspecified,
        header = Color.Unspecified,
        hover = Color.Unspecified,
        textSecondaryInverted = Color.Unspecified,
        backgroundInverted = Color.Unspecified,
        textPrimaryInverted = Color.Unspecified,
        divider = Color.Unspecified
    )
}

val LocalExtendedTypography = staticCompositionLocalOf {
    MeteoriteTypography
}

@Composable
fun MeteoriteLandingsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    ExtendedTheme.isLight = !darkTheme
    val colors = getColorTheme(darkTheme)
    val extendedColors = getExtendedColorsTheme(darkTheme)
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colorScheme = colors,
            shapes = Shapes,
            content = content
        )
    }
}

@Composable
fun getExtendedColorsTheme(darkTheme: Boolean): ExtendedColors = if (darkTheme) {
    ExtendedColors(
        textPrimary = textPrimaryDark,
        textSecondary = textSecondaryDark,
        highlight = highlightDark,
        header = headerDark,
        hover = hoverDark,
        backgroundInverted = background,
        textPrimaryInverted = textPrimary,
        textSecondaryInverted = textSecondary,
        divider = dividerDark,
    )
} else {
    ExtendedColors(
        textPrimary = textPrimary,
        textSecondary = textSecondary,
        highlight = highlight,
        header = header,
        hover = hover,
        backgroundInverted = backgroundDark,
        textPrimaryInverted = textPrimaryDark,
        textSecondaryInverted = textSecondaryDark,
        divider = divider,
    )
}


@Composable
private fun getColorTheme(darkTheme: Boolean) = if (darkTheme) {
    darkColorScheme(
        primary = colorPrimary,
        secondary = colorPrimaryDark,
        background = backgroundDark,
        surface = backgroundDark,
    )
} else {
    lightColorScheme(
        primary = colorPrimary,
        secondary = colorPrimary,
        background = background,
        surface = background,
    )
}


// Use with eg. ExtendedTheme.colors.tertiary
object ExtendedTheme {

    var isLight: Boolean = false

    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current

    val typography: MeteoriteTypography
        @Composable
        get() = LocalExtendedTypography.current

}