package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


data class MeteoriteView(
    val id: String?,
    val name: String,
    val yearString: String,
    val address: String,
    val type: String,
    val mass: String,
    val reclat: Double,
    val reclong: Double
)

@Composable
fun MeteoriteDetail(
    meteoriteView: MeteoriteView
) {
    val latitude = meteoriteView.reclat
    val longitude = meteoriteView.reclong

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Center,
    ) {

        // Google Map view with a marker at the given location
        val target = LatLng(latitude, longitude)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(target, 10f)
        }
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
                .weight(1f),  // This makes the GoogleMap fill all available space
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = target)
            )
        }
        LineDetail(icon = R.drawable.ic_globe, label = meteoriteView.address)
        LineDetail(icon = R.drawable.ic_weight, label = meteoriteView.mass)
        LineDetail(icon = R.drawable.ic_type, label = meteoriteView.type)
        LineDetail(icon = R.drawable.ic_crash, label = meteoriteView.yearString)
    }
}

@Composable
private fun LineDetail(
    @DrawableRes icon: Int,
    label: String
) {
    Row(
        verticalAlignment = CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(42.dp)
            .padding(horizontal = 16.dp)
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier
                .align(CenterVertically)
                .size(40.dp)
                .padding(end = 16.dp),
        )
        Text(
            text = label,
            textAlign = TextAlign.Start,
            style = ExtendedTheme.typography.body2,
            color = ExtendedTheme.colors.textPrimary
        )
    }
}


@Preview("MeteoriteDetail Dark")
@Composable
fun MeteoriteDetailDark() {
    val sample = "test"
    MeteoriteLandingsTheme(
        darkTheme = true
    ) {
        MeteoriteDetail(
            meteoriteView = MeteoriteView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                address = "address $sample",
                type = "distance $sample",
                mass = "mass",
                reclat = 0.0,
                reclong = 0.0
            )
        )
    }
}


@Preview("MeteoriteDetail Light")
@Composable
fun MeteoriteDetailLight() {
    val sample = "test"
    MeteoriteLandingsTheme(
        darkTheme = false
    ) {
        MeteoriteDetail(
            meteoriteView = MeteoriteView(
                id = sample,
                name = "name $sample",
                yearString = "yearString $sample",
                address = "address $sample",
                type = "distance $sample",
                mass = "mass",
                reclat = 0.0,
                reclong = 0.0
            )
        )
    }
}
