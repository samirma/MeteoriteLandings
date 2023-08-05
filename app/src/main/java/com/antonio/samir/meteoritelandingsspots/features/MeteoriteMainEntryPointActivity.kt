package com.antonio.samir.meteoritelandingsspots.features


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.theme.MeteoriteLandingsTheme
import com.antonio.samir.meteoritelandingsspots.features.detail.DetailScreen
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteDetailViewModel
import com.antonio.samir.meteoritelandingsspots.features.list.ListScreenNavigation
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.service.monetization.MonetizationInterface
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

@OptIn(ExperimentalAnimationApi::class)
class MeteoriteMainEntryPointActivity : ComponentActivity() {

    private val monetization: MonetizationInterface by inject()

    private val detailViewModel: MeteoriteDetailViewModel by viewModel()

    private val listViewModel: MeteoriteListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        monetization.start(lifecycleScope, this)

        setContent { MeteoriteLandingsTheme { Navigation() } }
    }

    @OptIn(ExperimentalComposeUiApi::class, FlowPreview::class, ExperimentalCoroutinesApi::class)
    @Composable
    private fun Navigation() {
        val navController = rememberNavController()
        NavHost(
            navController, startDestination = Screen.meteoriteList.route,
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            composable(Screen.meteoriteList.route) {
                ListScreenNavigation(
                    listViewModel,
                    navController
                )
            }
            composable(
                "${Screen.meteoriteDetail.route}/{meteoriteId}",
                arguments = listOf(navArgument("meteoriteId") { type = NavType.StringType })
            ) { backStackEntry ->
                val meteoriteId = backStackEntry.arguments?.getString("meteoriteId")!!
                DetailScreen(meteoriteId, navController, detailViewModel)
            }
        }
    }
}
