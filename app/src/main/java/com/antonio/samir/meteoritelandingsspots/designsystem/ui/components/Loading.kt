package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

const val TAG_LOADING = "loading"

@Composable
fun Loading(modifier: Modifier = Modifier) {
    val description = stringResource(R.string.loading_resources)
    Box(
        // Every call site passes fillMaxSize and expects a centred spinner; the previous version
        // used Arrangement.Top, so it sat against the top edge instead.
        modifier = modifier
            .testTag(TAG_LOADING)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@PreviewLightDark
@Preview
@Composable
private fun LoadingPreview() {
    MeteoriteLandingsTheme {
        Loading()
    }
}
