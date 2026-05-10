package com.antonio.samir.meteoritelandingsspots.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.antonio.samir.meteoritelandingsspots.BuildConfig
import com.antonio.samir.meteoritelandingsspots.common.userCase.IsDarkTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugNavigation
import com.antonio.samir.meteoritelandingsspots.features.detail.DetailScreenNavigation
import com.antonio.samir.meteoritelandingsspots.features.list.ListScreenNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var isDarkTheme: IsDarkTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            val darkThemeFlow = isDarkTheme(Unit)

            val darkTheme by darkThemeFlow.collectAsState(initial = false)

            val backStack = rememberSaveable(
                saver = listSaver(
                    save = { it.toList() },
                    restore = { it.toMutableStateList() }
                )
            ) {
                mutableStateListOf<NavigationKey>(NavigationKey.List)
            }

            BackHandler(enabled = backStack.size > 1) {
                backStack.removeLastOrNull()
            }

            MeteoriteLandingsTheme(
                darkTheme = darkTheme
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .safeDrawingPadding()
                ) {
                    Navigation(backStack)
                    if (BuildConfig.DEBUG) {
                        Button(
                            onClick = { backStack.add(NavigationKey.Debug) },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Text(text = "Debug")
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun Navigation(
        backStack: androidx.compose.runtime.snapshots.SnapshotStateList<NavigationKey>
    ) {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryProvider = { key ->
                when (key) {
                    is NavigationKey.List -> {
                        NavEntry(key) {
                            ListScreenNavigation(
                                onItemClick = { meteoriteId ->
                                    backStack.add(NavigationKey.Detail(meteoriteId))
                                }
                            )
                        }
                    }
                    is NavigationKey.Detail -> {
                        NavEntry(key) {
                            DetailScreenNavigation(
                                meteoriteId = key.meteoriteId,
                                onBack = { backStack.removeLastOrNull() }
                            )
                        }
                    }
                    is NavigationKey.Debug -> {
                        NavEntry(key) {
                            DebugNavigation()
                        }
                    }
                }
            }
        )

    }
}
