package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

const val TAG_OFFLINE_BANNER = "offline_banner"
const val TAG_LOCATION_BANNER = "location_banner"

/**
 * Shown while the device has no usable connection. The app is offline-first — the list is served
 * from the bundled database — so this is informational, not an error state.
 */
@Composable
fun OfflineBanner(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TAG_OFFLINE_BANNER),
        ) {
            Text(
                text = stringResource(R.string.no_network),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}

/**
 * Explains why distances are missing and offers the way to fix it. [onGrant] triggers the system
 * dialog while it can still be shown; after a permanent denial the only route is app settings.
 */
@Composable
fun LocationPermissionBanner(
    visible: Boolean,
    permanentlyDenied: Boolean,
    onGrant: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        Surface(
            color = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TAG_LOCATION_BANNER),
        ) {
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_globe),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(
                        text = stringResource(R.string.location_permission_rationale),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                TextButton(onClick = onGrant) {
                    Text(
                        stringResource(
                            if (permanentlyDenied) {
                                R.string.location_permission_settings
                            } else {
                                R.string.location_permission_allow
                            }
                        )
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun BannersPreview() {
    MeteoriteLandingsTheme {
        Column {
            OfflineBanner(visible = true)
            LocationPermissionBanner(
                visible = true,
                permanentlyDenied = false,
                onGrant = {},
            )
        }
    }
}
