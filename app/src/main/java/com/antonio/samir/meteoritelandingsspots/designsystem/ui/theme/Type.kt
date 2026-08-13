package com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.antonio.samir.meteoritelandingsspots.R

val Roboto = FontFamily(
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_blackitalic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.roboto_thin, FontWeight.Thin),
    Font(R.font.roboto_thinitalic, FontWeight.Thin, FontStyle.Italic),
)

/**
 * A real Material 3 [Typography], using the Roboto family the app already bundles.
 *
 * Previously the app carried its own object with Material 2 role names (`h4`, `subtitle1`, …)
 * that was never handed to `MaterialTheme`, so M3 components rendered with baseline typography
 * while the app's own text used a second, disconnected scale — and the twelve bundled font files
 * shipped without ever being applied to anything.
 */
val MeteoriteTypography = Typography().run {
    copy(
        displayLarge = displayLarge.withRoboto(),
        displayMedium = displayMedium.withRoboto(),
        displaySmall = displaySmall.withRoboto(),
        headlineLarge = headlineLarge.withRoboto(FontWeight.SemiBold),
        headlineMedium = headlineMedium.withRoboto(FontWeight.SemiBold),
        headlineSmall = headlineSmall.withRoboto(FontWeight.SemiBold),
        titleLarge = titleLarge.withRoboto(FontWeight.SemiBold),
        titleMedium = titleMedium.withRoboto(FontWeight.Medium),
        titleSmall = titleSmall.withRoboto(FontWeight.Medium),
        bodyLarge = bodyLarge.withRoboto(),
        bodyMedium = bodyMedium.withRoboto(),
        bodySmall = bodySmall.withRoboto(),
        labelLarge = labelLarge.withRoboto(FontWeight.SemiBold),
        labelMedium = labelMedium.withRoboto(FontWeight.Medium),
        labelSmall = labelSmall.withRoboto(FontWeight.Medium, letterSpacing = 0.5.sp),
    )
}

private fun TextStyle.withRoboto(
    weight: FontWeight = fontWeight ?: FontWeight.Normal,
    letterSpacing: androidx.compose.ui.unit.TextUnit = this.letterSpacing,
) = copy(fontFamily = Roboto, fontWeight = weight, letterSpacing = letterSpacing)
