package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String = "",
    placeholderText: String = "",
    onNavigateBack: () -> Unit = {},
    onSearch: (query: String) -> Unit,
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var text by remember { mutableStateOf(searchText) }

    val clear = {
        if (text.isNotBlank()) {
            text = ""
            onSearch(text)
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = {
            clear()
            onNavigateBack()
        }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                modifier = Modifier,
                contentDescription = stringResource(R.string.close_search),
                tint = ExtendedTheme.colors.highlight
            )
        }

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp)
                .onFocusChanged { focusState ->
                    showClearButton = (focusState.isFocused)
                }
                .focusRequester(focusRequester),
            value = text,
            onValueChange = {
                text = it
            },
            placeholder = {
                Text(text = placeholderText, style = ExtendedTheme.typography.body2)
            },
            trailingIcon = {
                AnimatedVisibility(
                    visible = showClearButton,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = clear) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(R.string.clear)
                        )
                    }

                }
            },
            maxLines = 1,
            singleLine = true,
            keyboardOptions = Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                //keyboardController?.hide()
                onSearch(text)
            }),
        )

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
    }
}

@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Preview("SearchBar")
@Composable
fun SearchBarPreview() {

    var darkTheme by remember { mutableStateOf(true) }

    MeteoriteLandingsTheme(darkTheme = darkTheme) {
        Surface(
            Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxWidth()
        ) {
            SearchBar(
                modifier = Modifier,
                searchText = "search text",
                placeholderText = "placeholder"
            ) {}
        }
    }
}


