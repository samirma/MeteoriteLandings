package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme


@Composable
fun ToolbarButtons(
    modifier: Modifier,
    onDarkModeToggleClick: () -> Unit,
    onEnterSearch: () -> Unit
) {
    Row(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_search),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .height(24.dp)
                .width(24.dp)
                .clickable {
                    onEnterSearch()
                },
            colorFilter = ColorFilter.tint(ExtendedTheme.colors.highlight)
        )

        val darkModeIcon = if (ExtendedTheme.isLight) {
            R.drawable.ic_dark
        } else {
            R.drawable.ic_light
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

@Preview("ToolbarActions Light")
@Composable
fun ToolbarActionsLightPreview() {

    var darkTheme by remember { mutableStateOf(true) }

    MeteoriteLandingsTheme(darkTheme = darkTheme) {
        Surface(Modifier.background(MaterialTheme.colorScheme.background)) {
            ToolbarButtons(
                Modifier,
                { darkTheme = !darkTheme }
            ) {}
        }
    }
}

@Preview("ToolbarActions Dark")
@Composable
fun ToolbarActionsDarkPreview() {

    var darkTheme by remember { mutableStateOf(false) }

    MeteoriteLandingsTheme(darkTheme = darkTheme) {
        Surface(Modifier.background(MaterialTheme.colorScheme.background)) {
            ToolbarButtons(
                Modifier,
                { darkTheme = !darkTheme }
            ) {}
        }
    }
}
