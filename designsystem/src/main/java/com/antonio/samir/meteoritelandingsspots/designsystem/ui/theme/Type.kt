package com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.antonio.samir.meteoritelandingsspots.designsystem.R

private val Roboto = FontFamily(
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
    Font(R.font.roboto_thinitalic, FontWeight.Thin, FontStyle.Italic)
)

// Set of Material typography styles to start with
object MeteoriteTypography {
    val defaultFontFamily = Roboto
    val h4 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        letterSpacing = 0.sp
    )
    val h5 = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    )
    val h6 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    )
    val subtitle1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    )
    val subtitle2 = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    )
    val body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.5.sp
    )
    val body2 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        letterSpacing = 0.25.sp
    )
    val button = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        letterSpacing = 1.25.sp
    )
    val caption = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.4.sp
    )
    val overline = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        letterSpacing = 1.sp
    )
}
