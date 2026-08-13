package com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Full Material 3 colour roles built around the app's existing brand blue and backgrounds.
 *
 * The previous theme overrode only four roles and left every `on*`, `*Container`, `error` and
 * `outline` role at the M3 baseline purple, so components picked up colours from a palette the
 * app never chose. The light-theme primary is a darker blue than the brand tone because white
 * text on `#468BFF` is about 3:1 — below the contrast floor for body text.
 */
private val BrandBlue = Color(0xFF468BFF)
private val BrandBlueDeep = Color(0xFF1B5FD0)

val LightColorScheme = lightColorScheme(
    primary = BrandBlueDeep,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8E4FF),
    onPrimaryContainer = Color(0xFF001A43),
    secondary = Color(0xFF565E71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDAE2F9),
    onSecondaryContainer = Color(0xFF131C2B),
    tertiary = Color(0xFF705575),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFAD8FD),
    onTertiaryContainer = Color(0xFF28132E),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF9F9F9),
    onBackground = Color(0xFF1A1B1F),
    surface = Color(0xFFF9F9F9),
    onSurface = Color(0xFF1A1B1F),
    surfaceVariant = Color(0xFFE0E2EC),
    onSurfaceVariant = Color(0xFF44474F),
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF3F3F7),
    surfaceContainer = Color(0xFFEEEDF1),
    surfaceContainerHigh = Color(0xFFE8E7EC),
    surfaceContainerHighest = Color(0xFFE2E2E6),
    outline = Color(0xFF74777F),
    outlineVariant = Color(0xFFC4C6D0),
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F0F4),
    inversePrimary = BrandBlue,
    scrim = Color.Black,
)

val DarkColorScheme = darkColorScheme(
    primary = BrandBlue,
    onPrimary = Color(0xFF002E6B),
    primaryContainer = Color(0xFF00458F),
    onPrimaryContainer = Color(0xFFD8E4FF),
    secondary = Color(0xFFBEC6DC),
    onSecondary = Color(0xFF283041),
    secondaryContainer = Color(0xFF3E4759),
    onSecondaryContainer = Color(0xFFDAE2F9),
    tertiary = Color(0xFFDDBCE0),
    onTertiary = Color(0xFF3F2844),
    tertiaryContainer = Color(0xFF573E5C),
    onTertiaryContainer = Color(0xFFFAD8FD),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF212326),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF212326),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF44474F),
    onSurfaceVariant = Color(0xFFC4C6D0),
    surfaceContainerLowest = Color(0xFF161719),
    surfaceContainerLow = Color(0xFF1E2023),
    surfaceContainer = Color(0xFF2F353D),
    surfaceContainerHigh = Color(0xFF383F48),
    surfaceContainerHighest = Color(0xFF434A54),
    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44474F),
    inverseSurface = Color(0xFFE3E2E6),
    inverseOnSurface = Color(0xFF1A1B1F),
    inversePrimary = BrandBlueDeep,
    scrim = Color.Black,
)
