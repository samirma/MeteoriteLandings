package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun AddressProgress(progress: Float, modifier: Modifier) {
    val isVisible = progress > 0 && progress < 100
    AnimatedVisibility(
        visible = isVisible,
        enter = expandHorizontally(
            expandFrom = Alignment.End
        ) + fadeIn(),
        exit = shrinkHorizontally(
            shrinkTowards = Alignment.End
        ) + fadeOut()
    ) {
        Row(
            modifier = modifier.background(Color.Magenta)
        ) {
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

