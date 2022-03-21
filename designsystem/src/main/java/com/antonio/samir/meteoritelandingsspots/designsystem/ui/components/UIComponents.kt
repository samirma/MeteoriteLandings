package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ToolbarActions(modifier: Modifier) {
    Row(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(24.dp)
                .width(24.dp),
            colorFilter = ColorFilter.tint(ExtendedTheme.colors.highlight)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_dark),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(50.dp)
                .width(50.dp)
                .padding(start = 26.dp),
            colorFilter = ColorFilter.tint(ExtendedTheme.colors.highlight)
        )

    }
}

@Preview("ToolbarActions")
@Composable
fun ToolbarActionsPreview() {
    MeteoriteLandingsTheme(darkTheme = false) {
        ToolbarActions(
            Modifier
                .padding(
                    horizontal = 80.dp
                )
        )
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Progress(modifier: Modifier) {
    Row(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(24.dp)
                .width(24.dp),
            colorFilter = ColorFilter.tint(ExtendedTheme.colors.highlight)
        )
        Image(
            painter = painterResource(id = R.drawable.ic_dark),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(50.dp)
                .width(50.dp)
                .padding(start = 26.dp),
            colorFilter = ColorFilter.tint(ExtendedTheme.colors.highlight)
        )

    }
}

@Preview("ToolbarActions")
@Composable
fun ProgressPreview() {
    MeteoriteLandingsTheme(darkTheme = false) {
        ToolbarActions(
            Modifier
                .padding(
                    horizontal = 80.dp
                )
        )
    }
}

