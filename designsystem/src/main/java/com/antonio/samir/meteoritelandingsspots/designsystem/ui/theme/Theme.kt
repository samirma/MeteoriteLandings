package com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Typography
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.antonio.samir.meteoritelandingsspots.designsystem.R

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
        textPrimaryInverted = Color.Unspecified
    )
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
        textPrimary = colorResource(R.color.textPrimaryDark),
        textSecondary = colorResource(R.color.textSecondaryDark),
        highlight = colorResource(R.color.highlightDark),
        header = colorResource(R.color.headerDark),
        hover = colorResource(R.color.hoverDark),
        backgroundInverted = colorResource(R.color.background),
        textPrimaryInverted = colorResource(R.color.textPrimary),
        textSecondaryInverted = colorResource(R.color.textSecondary),
    )
} else {
    ExtendedColors(
        textPrimary = colorResource(R.color.textPrimary),
        textSecondary = colorResource(R.color.textSecondary),
        highlight = colorResource(R.color.highlight),
        header = colorResource(R.color.header),
        hover = colorResource(R.color.hover),
        backgroundInverted = colorResource(R.color.backgroundDark),
        textPrimaryInverted = colorResource(R.color.textPrimaryDark),
        textSecondaryInverted = colorResource(R.color.textSecondaryDark)
    )
}


@Composable
private fun getColorTheme(darkTheme: Boolean) = if (darkTheme) {
    darkColorScheme(
        primary = colorResource(R.color.colorPrimaryDark),
        secondary = colorResource(R.color.colorPrimaryDark),
        background = colorResource(R.color.backgroundDark),
        surface = colorResource(R.color.backgroundDark),
    )
} else {
    lightColorScheme(
        primary = colorResource(R.color.colorPrimary),
        secondary = colorResource(R.color.colorPrimary),
        background = colorResource(R.color.background),
        surface = colorResource(R.color.background),
    )
}


// Use with eg. ExtendedTheme.colors.tertiary
object ExtendedTheme {

    var isLight: Boolean = false

    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current

    val typography: Typography
        @Composable
        get() = meteoriteTypography

}