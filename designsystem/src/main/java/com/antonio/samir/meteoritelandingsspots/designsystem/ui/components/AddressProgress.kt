package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun AddressProgress(progress: Float, modifier: Modifier) {
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
                    style = MaterialTheme.typography.subtitle2,
                    fontSize = 8.sp,
                    color = ExtendedTheme.colors.textPrimaryInverted
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = stringResource(R.string.loading_resources),
                    style = MaterialTheme.typography.overline,
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
