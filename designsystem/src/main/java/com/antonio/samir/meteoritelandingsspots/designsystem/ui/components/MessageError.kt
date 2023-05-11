package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme

@Composable
fun MessageError(
    @StringRes message: Int,
    modifier: Modifier = Modifier
) = MessageError(message = stringResource(id = message), modifier = modifier)

@Composable
fun MessageError(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.message_titile),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h6,
            color = ExtendedTheme.colors.textPrimary
        )
        Text(
            text = message,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body1,
            color = ExtendedTheme.colors.textSecondary
        )
    }
}
