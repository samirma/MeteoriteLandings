package com.antonio.samir.meteoritelandingsspots.features

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugNavigation
import com.antonio.samir.meteoritelandingsspots.features.detail.DetailScreenNavigation
import com.antonio.samir.meteoritelandingsspots.features.list.ListScreenNavigation

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun MeteoriteNavDisplay(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier,
) {
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        // The ViewModel decorator is the important addition. Without it every hiltViewModel()
        // resolves against the Activity's ViewModelStore, so the detail ViewModel behaves like a
        // singleton that keeps the previously opened meteorite and is never cleared on pop.
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        // Two panes side by side once the window is wide enough. Under targetSdk 37 the platform
        // ignores the portrait lock at sw >= 600dp, so this is required rather than optional.
        sceneStrategies = listOf(listDetailStrategy),
        entryProvider = entryProvider {
            entry<NavigationKey.List>(
                metadata = ListDetailSceneStrategy.listPane(
                    detailPlaceholder = { DetailPlaceholder() }
                )
            ) {
                ListScreenNavigation(
                    onItemClick = { meteoriteId ->
                        backStack.add(NavigationKey.Detail(meteoriteId))
                    }
                )
            }

            entry<NavigationKey.Detail>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                DetailScreenNavigation(
                    meteoriteId = key.meteoriteId,
                    onBack = { backStack.removeLastOrNull() },
                )
            }

            entry<NavigationKey.Debug> {
                DebugNavigation(onBack = { backStack.removeLastOrNull() })
            }
        },
    )
}

/** Shown in the detail pane on wide windows before anything is selected. */
@Composable
private fun DetailPlaceholder(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(R.string.detail_placeholder),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
