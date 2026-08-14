package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

const val TAG_ADDRESS_PROGRESS = "address_progress"

/**
 * Reverse-geocoding progress.
 *
 * Replaces a hand-drawn pill built from a fixed-size black drawable with 6sp/8sp text on top —
 * unreadable at any font scale, and invisible to TalkBack.
 */
@Composable
fun AddressProgress(
    progress: Float,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = progress > 0f && progress < COMPLETE,
        modifier = modifier,
    ) {
        val percent = stringResource(R.string.loading_addresses_progress, progress)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .testTag(TAG_ADDRESS_PROGRESS)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .semantics { contentDescription = percent },
        ) {
            Text(
                text = percent,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LinearProgressIndicator(
                progress = { progress / COMPLETE },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }
    }
}

private const val COMPLETE = 100f

@PreviewLightDark
@Composable
private fun AddressProgressPreview() {
    MeteoriteLandingsTheme {
        AddressProgress(progress = 42f)
    }
}
