package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    isScrollOnTop: Boolean,
    modifier: Modifier = Modifier,
    onSearch: (query: String) -> Unit = {},
    onDarkModeToggleClick: () -> Unit = {},
) {

    val isSearch = remember { mutableStateOf(false) }

    if (isSearch.value) {
        SearchBar(
            placeholderText = stringResource(R.string.search_placeholder),
            onNavigateBack = {
                isSearch.value = false
            },
            onSearch = onSearch
        )
    } else {

        val height = if (isScrollOnTop) {
            72.dp
        } else {
            276.dp
        }

        Box(
            modifier = modifier
                .height(height)
                .background(ExtendedTheme.colors.header),
        ) {
            AnimatedVisibility(
                visible = !isScrollOnTop,
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
                    style = ExtendedTheme.typography.h4
                )
            }
            Row(
                modifier = Modifier
                    .align(if (isScrollOnTop) Alignment.TopCenter else Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                AnimatedVisibility(
                    visible = isScrollOnTop,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.weight(weight = 1f, fill = true)
                ) {
                    Text(
                        text = stringResource(R.string.title_header),
                        textAlign = TextAlign.Start,
                        color = ExtendedTheme.colors.textPrimary,
                        style = ExtendedTheme.typography.h6
                    )
                }
                ToolbarButtons(
                    modifier = Modifier.align(Alignment.Bottom),
                    onDarkModeToggleClick = onDarkModeToggleClick,
                    onEnterSearch = { isSearch.value = true }
                )
            }
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Header Collapsed")
@Composable
fun HeaderPreview() {
    MeteoriteLandingsTheme(darkTheme = true) {
        Surface {
            Header(true) {}
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Preview("Header Expanded")
@Composable
fun HeaderExpandedPreview() {
    MeteoriteLandingsTheme(darkTheme = true) {
        Surface {
            Header(false) {}
        }
    }
}