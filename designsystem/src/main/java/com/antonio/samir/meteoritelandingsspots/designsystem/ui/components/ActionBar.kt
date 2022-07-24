
package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme


@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun ActionBar(
    title: String,
    onItemClick: (() -> Unit) = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(MaterialTheme.colors.background)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_back),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.Top)
                .padding(top = 2.dp)
                .height(24.dp)
                .width(24.dp)
                .clickable {
                    onItemClick()
                },
            colorFilter = ColorFilter.tint(ExtendedTheme.colors.highlight)
        )
        Text(
            modifier = Modifier
                .align(Alignment.Top)
                .fillMaxWidth()
                .padding(start = 16.dp),
            text = title,
            textAlign = TextAlign.Start,
            color = ExtendedTheme.colors.textPrimary,
            style = MaterialTheme.typography.h6
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Preview("ActionBar Light")
@Composable
fun ActionBarLightPreview() {
    MeteoriteLandingsTheme(darkTheme = false) {
        ActionBar("ActionBar Light")
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class)
@Preview("ActionBar Dark")
@Composable
fun ActionBarDarkPreview() {
    MeteoriteLandingsTheme(darkTheme = true) {
        ActionBar("ActionBar Dark")
    }
}

