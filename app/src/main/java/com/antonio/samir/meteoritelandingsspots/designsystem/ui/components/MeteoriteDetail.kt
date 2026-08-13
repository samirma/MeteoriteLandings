package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState

const val TAG_DETAIL_MAP = "detail_map"

@Immutable
data class MeteoriteView(
    val id: Int,
    val name: String,
    val yearString: String,
    val address: String,
    val type: String,
    val mass: String,
    val latitude: Double,
    val longitude: Double,
)

@Composable
fun MeteoriteDetail(
    meteoriteView: MeteoriteView,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        MeteoriteMap(
            meteoriteView = meteoriteView,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        LineDetail(R.drawable.ic_globe, R.string.detail_location, meteoriteView.address)
        LineDetail(R.drawable.ic_weight, R.string.detail_mass, meteoriteView.mass)
        LineDetail(R.drawable.ic_type, R.string.detail_type, meteoriteView.type)
        LineDetail(R.drawable.ic_crash, R.string.detail_year, meteoriteView.yearString)
    }
}

@Composable
private fun MeteoriteMap(
    meteoriteView: MeteoriteView,
    modifier: Modifier = Modifier,
) {
    val target = remember(meteoriteView.latitude, meteoriteView.longitude) {
        LatLng(meteoriteView.latitude, meteoriteView.longitude)
    }

    // Keyed on the meteorite id: without a key the camera state was reused across meteorites,
    // which is what commit 2d0a7f3 worked around with a LaunchedEffect that re-centred the map
    // on every coordinate change — and which also fought any manual pan.
    val cameraPositionState = rememberCameraPositionState(key = meteoriteView.id.toString()) {
        position = CameraPosition.fromLatLngZoom(target, DEFAULT_ZOOM)
    }
    val markerState = rememberUpdatedMarkerState(position = target)

    val isDark = MeteoriteTheme.isDark
    val mapStyle = if (isDark) {
        remember { MapStyleOptions(DARK_MAP_STYLE) }
    } else {
        null
    }

    val description = stringResource(R.string.detail_map_description, meteoriteView.name)
    GoogleMap(
        modifier = modifier
            .testTag(TAG_DETAIL_MAP)
            .semantics { contentDescription = description },
        cameraPositionState = cameraPositionState,
        properties = MapProperties(mapStyleOptions = mapStyle),
        uiSettings = MapUiSettings(zoomControlsEnabled = false, mapToolbarEnabled = false),
    ) {
        Marker(state = markerState, title = meteoriteView.name, snippet = meteoriteView.address)
    }
}

@Composable
private fun LineDetail(
    @DrawableRes icon: Int,
    labelRes: Int,
    value: String,
) {
    val label = stringResource(labelRes)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .semantics { contentDescription = "$label: $value" },
    ) {
        // Image, not Icon: these are multi-colour vectors that a tint would flatten.
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 16.dp),
        )
    }
}

private const val DEFAULT_ZOOM = 10f

/**
 * Minimal dark map style so the map does not stay bright white inside the dark theme.
 */
private const val DARK_MAP_STYLE = """
[
  {"elementType":"geometry","stylers":[{"color":"#242f3e"}]},
  {"elementType":"labels.text.stroke","stylers":[{"color":"#242f3e"}]},
  {"elementType":"labels.text.fill","stylers":[{"color":"#746855"}]},
  {"featureType":"administrative.locality","elementType":"labels.text.fill","stylers":[{"color":"#d59563"}]},
  {"featureType":"poi","elementType":"labels.text.fill","stylers":[{"color":"#d59563"}]},
  {"featureType":"road","elementType":"geometry","stylers":[{"color":"#38414e"}]},
  {"featureType":"road","elementType":"geometry.stroke","stylers":[{"color":"#212a37"}]},
  {"featureType":"road","elementType":"labels.text.fill","stylers":[{"color":"#9ca5b3"}]},
  {"featureType":"water","elementType":"geometry","stylers":[{"color":"#17263c"}]},
  {"featureType":"water","elementType":"labels.text.fill","stylers":[{"color":"#515c6d"}]}
]
"""

@PreviewLightDark
@Preview
@Composable
private fun MeteoriteDetailPreview() {
    MeteoriteLandingsTheme {
        MeteoriteDetail(
            meteoriteView = MeteoriteView(
                id = 1,
                name = "Aachen",
                yearString = "1880",
                address = "Aachen, Nordrhein-Westfalen, Germany",
                type = "L5",
                mass = "21 g",
                latitude = 50.775,
                longitude = 6.08333,
            )
        )
    }
}
