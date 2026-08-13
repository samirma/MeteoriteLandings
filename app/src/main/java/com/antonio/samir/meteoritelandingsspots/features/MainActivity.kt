package com.antonio.samir.meteoritelandingsspots.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.rememberNavBackStack
import com.antonio.samir.meteoritelandingsspots.BuildConfig
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        // Edge-to-edge has had no opt-out since targetSdk 35; opting in explicitly means the
        // system bars are transparent and Compose owns the inset handling.
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Hold the splash until the persisted theme has been read, so the first frame is already
        // the right colour instead of flashing light before DataStore emits.
        splashScreen.setKeepOnScreenCondition { !viewModel.themeState.value.isResolved }

        setContent {
            val themeState by viewModel.themeState.collectAsStateWithLifecycle()
            val darkTheme = themeState.isDark ?: isSystemInDarkTheme()

            MeteoriteLandingsTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    val backStack = rememberNavBackStack(NavigationKey.List)

                    Box(Modifier.fillMaxSize()) {
                        // No BackHandler here: NavDisplay registers its own predictive-back
                        // handler, and a second one competing for the same stack could double-pop.
                        MeteoriteNavDisplay(backStack = backStack)

                        if (BuildConfig.DEBUG) {
                            ExtendedFloatingActionButton(
                                onClick = { backStack.add(NavigationKey.Debug) },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp),
                            ) {
                                Text(text = "Debug")
                            }
                        }
                    }
                }
            }
        }
    }
}
