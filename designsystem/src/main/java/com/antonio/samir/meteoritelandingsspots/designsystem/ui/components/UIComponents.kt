package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions.Companion.Default
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchText: String = "",
    placeholderText: String = "",
    onSearchTextChanged: (String) -> Unit = {},
    onClearClick: () -> Unit = {},
    onNavigateBack: () -> Unit = {},
    onSearch: (query: String) -> Unit,
) {
    var showClearButton by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { onNavigateBack() }) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                modifier = Modifier,
                contentDescription = stringResource(R.string.close_search)
            )
        }

        var text by remember { mutableStateOf(searchText) }

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
//            colors = TextFieldDefaults.textFieldColors(
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent,
//                cursorColor = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)
//            ),
            trailingIcon = {
                AnimatedVisibility(
                    visible = showClearButton,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    IconButton(onClick = {
                        onClearClick()
                        text = ""
                    }) {
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
                keyboardController?.hide()
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


@Preview("Progress Light")
@Composable
fun ProgressLightPreview() {

    MeteoriteLandingsTheme(darkTheme = false) {

        var state by remember { mutableStateOf(0.0f) }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AddressProgress(
                progress = state,
                modifier = Modifier
            )
            Button(
                onClick = { state = 10.0f },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = "Show")
            }
            Button(
                onClick = { state = 0.0f },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = "Hide")
            }
        }
    }
}

@Preview("Progress Dark")
@Composable
fun ProgressLightDark() {

    MeteoriteLandingsTheme(darkTheme = true) {

        var state by remember { mutableStateOf(0.0f) }

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AddressProgress(
                progress = state,
                modifier = Modifier
            )
            Button(
                onClick = { state = 10.0f },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = "Show")
            }
            Button(
                onClick = { state = 0.0f },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text(text = "Hide")
            }
        }
    }
}


@Composable
fun Shimmer(modifier: Modifier = Modifier) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    Box(
        modifier = modifier
            .width(152.dp)
            .height(20.dp)
            .shimmer(customShimmer = shimmer),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .background(Color.LightGray)
        )
    }
}


@Composable
fun Loading(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}
