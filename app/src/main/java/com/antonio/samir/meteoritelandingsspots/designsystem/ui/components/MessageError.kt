package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

const val TAG_ERROR_MESSAGE = "error_message"

@Composable
fun MessageError(
    @StringRes message: Int,
    modifier: Modifier = Modifier,
) = MessageError(message = stringResource(id = message), modifier = modifier)

@Composable
private fun MessageError(
    message: String,
    modifier: Modifier = Modifier,
) {
    Column(
        // Note the absence of fillMaxSize() here: it used to be applied *after* the caller's
        // modifier, which silently overrode whatever size the caller asked for.
        modifier = modifier
            .testTag(TAG_ERROR_MESSAGE)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(id = R.string.error_title),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun MessageErrorPreview() {
    MeteoriteLandingsTheme {
        MessageError(message = R.string.general_error)
    }
}
