package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme


@Composable
fun AddressProgress(progress: Float, modifier: Modifier = Modifier) {
    val isVisible = progress > 0f && progress < 100f
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.width(112.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_load_layout),
                contentDescription = "",
                modifier = Modifier.align(Alignment.CenterStart),
                colorFilter = ColorFilter.tint(ExtendedTheme.colors.backgroundInverted)
            )
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = String.format("%.2f", progress) + "%",
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.CenterVertically),
                    style = ExtendedTheme.typography.subtitle2,
                    fontSize = 8.sp,
                    color = ExtendedTheme.colors.textPrimaryInverted
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.loading_resources),
                    style = ExtendedTheme.typography.overline,
                    fontSize = 6.sp,
                    color = ExtendedTheme.colors.textSecondaryInverted
                )
            }
        }
    }

}

@Preview("AddressProgress Light")
@Composable
fun AddressProgresssLightPreview() {
    MeteoriteLandingsTheme(darkTheme = false) {
        AddressProgress(10.0f, Modifier)
    }
}

@Preview("AddressProgress Dark")
@Composable
fun AddressProgresssDarkPreview() {
    MeteoriteLandingsTheme(darkTheme = true) {
        AddressProgress(10.0f, Modifier)
    }
}


@Preview("Progress Light")
@Composable
fun ProgressLightPreview() {

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


@Preview("Progress Dark")
@Composable
fun ProgressLightDark() {

    MeteoriteLandingsTheme(darkTheme = true) {

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
