package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.designsystem.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.ExtendedTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme


@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@Composable
fun Header(
    headerState: HeaderState,
    modifier: Modifier = Modifier,
    onEnterSearch: () -> Unit = {},
    onExitSearch: () -> Unit = {},
    onSearch: (query: String) -> Unit = {},
    onDarkModeToggleClick: () -> Unit,
) {

    val isCollapsed = headerState.isCollapsed()
    val isSearch = headerState.isSearch()

    var headerModifier = modifier

    if (!isCollapsed) {
        headerModifier = headerModifier.height(72.dp)
    }

    if (isSearch) {
        SearchBar(
            placeholderText = stringResource(R.string.search_placeholder),
            onNavigateBack = onExitSearch,
            onSearch = onSearch
        )
    } else {
        Box(
            modifier = headerModifier.background(ExtendedTheme.colors.header),
        ) {
            AnimatedVisibility(
                visible = !isCollapsed,
                modifier = Modifier.align(Alignment.Center),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically(),
            ) {
                Text(
                    text = stringResource(R.string.title_header),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(
                            horizontal = 30.dp
                        ),
                    color = ExtendedTheme.colors.textPrimary,
                    style = MaterialTheme.typography.h4
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(72.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(
                    visible = isCollapsed,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.weight(weight = 1f, fill = true)
                ) {
                    Text(
                        text = stringResource(R.string.title_header),
                        textAlign = TextAlign.Start,
                        color = ExtendedTheme.colors.textPrimary,
                        style = MaterialTheme.typography.h6
                    )
                }
                ToolbarButtons(
                    modifier = Modifier,
                    onDarkModeToggleClick = onDarkModeToggleClick,
                    onEnterSearch = onEnterSearch
                )
            }
        }
    }
}


sealed class HeaderState {
    object Collapsed : HeaderState()
    object Expanded : HeaderState()
    object Search : HeaderState()

    fun isCollapsed(): Boolean = this == Collapsed
    fun isSearch(): Boolean = this == Search

}

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Header Collapsed")
@Composable
fun HeaderPreview() {
    MeteoriteLandingsTheme(darkTheme = true) {
        Surface() {
            Header(HeaderState.Collapsed) {}
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Header Expanded")
@Composable
fun HeaderExpandedPreview() {
    MeteoriteLandingsTheme(darkTheme = true) {
        Surface() {
            Header(HeaderState.Expanded) {}
        }
    }
}