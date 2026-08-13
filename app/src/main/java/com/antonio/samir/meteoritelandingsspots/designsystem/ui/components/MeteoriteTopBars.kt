package com.antonio.samir.meteoritelandingsspots.designsystem.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme

const val TAG_SEARCH_BUTTON = "search_button"
const val TAG_SEARCH_FIELD = "search_field"
const val TAG_THEME_TOGGLE = "theme_toggle"
const val TAG_BACK_BUTTON = "back_button"

/**
 * List screen top bar.
 *
 * A real [LargeTopAppBar] with a scroll behaviour, rather than the previous hand-built Box that
 * snapped between 276dp and 72dp with no animation and handled no window insets.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeteoriteListTopBar(
    isDarkTheme: Boolean,
    onEnterSearch: () -> Unit,
    onDarkModeToggleClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    LargeTopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = stringResource(R.string.title_header),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = {
            IconButton(
                onClick = onEnterSearch,
                modifier = Modifier.testTag(TAG_SEARCH_BUTTON),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    // The old tappable Images had contentDescription = "", so the search and
                    // theme controls were invisible to TalkBack.
                    contentDescription = stringResource(R.string.search),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
            val themeState = stringResource(
                if (isDarkTheme) R.string.theme_state_dark else R.string.theme_state_light
            )
            IconToggleButton(
                checked = isDarkTheme,
                onCheckedChange = { onDarkModeToggleClick() },
                modifier = Modifier
                    .testTag(TAG_THEME_TOGGLE)
                    .semantics { stateDescription = themeState },
            ) {
                Icon(
                    painter = painterResource(
                        if (isDarkTheme) R.drawable.ic_light else R.drawable.ic_dark
                    ),
                    contentDescription = stringResource(R.string.toggle_theme),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
        scrollBehavior = scrollBehavior,
    )
}

/**
 * Detail screen top bar.
 *
 * Deliberately exposes no `TopAppBarScrollBehavior` parameter: that type is still experimental,
 * so having it in the signature would force `@OptIn` on every caller.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeteoriteDetailTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        navigationIcon = {
            IconButton(onClick = onBack, modifier = Modifier.testTag(TAG_BACK_BUTTON)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.navigate_up),
                )
            }
        },
    )
}

/**
 * Search bar for the list screen.
 *
 * Renamed from `SearchBar` so it no longer shadows `androidx.compose.material3.SearchBar`, and
 * fully state-hoisted — the query used to live in a plain `remember` inside the component, so it
 * was lost on rotation and the caller could neither read nor reset it.
 */
@Composable
fun MeteoriteSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            // This sits in a Scaffold topBar slot, which is responsible for its own insets.
            // Without this the field draws underneath the status bar.
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(TAG_SEARCH_FIELD)
            .focusRequester(focusRequester),
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(R.string.search_placeholder)) },
        leadingIcon = {
            IconButton(onClick = {
                onQueryChange("")
                onClose()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.close_search),
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.clear),
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboardController?.hide() }),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Preview
@Composable
private fun MeteoriteDetailTopBarPreview() {
    MeteoriteLandingsTheme {
        MeteoriteDetailTopBar(title = "Aachen", onBack = {})
    }
}

@PreviewLightDark
@Preview
@Composable
private fun MeteoriteSearchFieldPreview() {
    MeteoriteLandingsTheme {
        MeteoriteSearchField(query = "Aachen", onQueryChange = {}, onClose = {})
    }
}
