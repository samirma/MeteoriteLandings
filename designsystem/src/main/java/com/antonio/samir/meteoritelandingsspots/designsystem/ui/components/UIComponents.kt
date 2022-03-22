package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
fun ToolbarButtons(modifier: Modifier, onDarkModeToggleClick: () -> Unit) {
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

        val darkModeIcon = if (MaterialTheme.colors.isLight) {
            R.drawable.ic_light
        } else {
            R.drawable.ic_dark
        }

        Image(
            painter = painterResource(id = darkModeIcon),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 26.dp)
                .clickable {
                    onDarkModeToggleClick()
                },
            colorFilter = ColorFilter.tint(ExtendedTheme.colors.highlight)
        )

    }
}

@Preview("ToolbarActions")
@Composable
fun ToolbarActionsPreview() {

    var darkTheme by remember { mutableStateOf(true) }

    MeteoriteLandingsTheme(darkTheme = darkTheme) {
        Surface(Modifier.background(MaterialTheme.colors.background)) {
            ToolbarButtons(
                Modifier
            ) { darkTheme = !darkTheme }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddressProgress(progress: Float, modifier: Modifier) {
    val isVisible = progress > 0 && progress < 100
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier
    ) {
        Row {
            Text(text = "$progress loading")
        }
    }

}

@Preview("ProgressPreview")
@Composable
fun ProgressPreview() {

    MeteoriteLandingsTheme(darkTheme = false) {

        var state by remember { mutableStateOf(0.0f) }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AddressProgress(
                progress = state,
                modifier = Modifier
            )
            Button(
                onClick = { state = 10.0f },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = "Show")
            }
            Button(
                onClick = { state = 0.0f },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = "Hide")
            }
        }
    }
}

