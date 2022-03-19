package com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
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
    val highlight: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        textPrimary = Color.Unspecified,
        textSecondary = Color.Unspecified,
        highlight = Color.Unspecified
    )
}

@Composable
fun MeteoriteLandingsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = getColorTheme(darkTheme)
    val extendedColors = getExtendedColorsTheme(darkTheme)
    CompositionLocalProvider(LocalExtendedColors provides extendedColors) {
        MaterialTheme(
            colors = colors,
            typography = Typography,
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
    )
} else {
    ExtendedColors(
        textPrimary = colorResource(R.color.textPrimary),
        textSecondary = colorResource(R.color.textSecondary),
        highlight = colorResource(R.color.highlight),
    )
}


@Composable
private fun getColorTheme(darkTheme: Boolean) = if (darkTheme) {
    darkColors(
        primary = colorResource(R.color.colorPrimaryDark),
        primaryVariant = colorResource(R.color.colorPrimaryDark),
        secondary = colorResource(R.color.colorPrimaryDark),
        background = colorResource(R.color.backgroundDark),
        surface = colorResource(R.color.surfaceDark),
    )
} else {
    lightColors(
        primary = colorResource(R.color.colorPrimary),
        primaryVariant = colorResource(R.color.colorPrimary),
        secondary = colorResource(R.color.colorPrimary),
        background = colorResource(R.color.background),
        surface = colorResource(R.color.surface),
    )
}


// Use with eg. ExtendedTheme.colors.tertiary
object ExtendedTheme {
    val colors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}