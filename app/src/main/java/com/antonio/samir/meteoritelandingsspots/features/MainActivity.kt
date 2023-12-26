package com.antonio.samir.meteoritelandingsspots.features


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.userCase.IsDarkTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.Route.DEBUG
import com.antonio.samir.meteoritelandingsspots.features.Route.DETAIL
import com.antonio.samir.meteoritelandingsspots.features.Route.LIST
import com.antonio.samir.meteoritelandingsspots.features.Route.METEORITE_ID_ARG
import com.antonio.samir.meteoritelandingsspots.features.debug.DebugNavigation
import com.antonio.samir.meteoritelandingsspots.features.detail.DetailScreenNavigation
import com.antonio.samir.meteoritelandingsspots.features.list.ListScreenNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@OptIn(ExperimentalAnimationApi::class)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var isDarkTheme: IsDarkTheme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_meteorite_list)

        findViewById<ComposeView>(R.id.compose_view).setContent {

            val darkTheme = isDarkTheme(Unit).collectAsState(initial = false).value
            MeteoriteLandingsTheme(
                darkTheme = darkTheme
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Navigation()
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun Navigation() {
        val navController = rememberNavController()
        NavHost(navController, startDestination = LIST) {
            composable(LIST) {
                ListScreenNavigation(navController = navController)
            }
            composable(DETAIL) { backStackEntry ->
                val appId = backStackEntry.arguments?.getString(METEORITE_ID_ARG).orEmpty()
                DetailScreenNavigation(
                    navController = navController,
                    meteoriteId = appId
                )
            }
            composable(DEBUG) { backStackEntry ->
                DebugNavigation()
            }
        }

    }
}
