package com.antonio.samir.meteoritelandingsspots.features


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.IsDarkTheme
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.AddressProgress
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

@OptIn(ExperimentalAnimationApi::class, FlowPreview::class, ExperimentalCoroutinesApi::class)
class MeteoriteMainEntryPointActivity : AppCompatActivity() {

    private val monetization: MonetizationInterface by inject()

    private val isDarkTheme: IsDarkTheme by inject()

    private val detailViewModel: MeteoriteDetailViewModel by viewModel()

    private val listViewModel: MeteoriteListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_meteorite_list)

        monetization.start(lifecycleScope, this)

        findViewById<ComposeView>(R.id.compose_view).setContent {
            val addressProgress by listViewModel.addressStatus.collectAsState()
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
                    (addressProgress as? ResultOf.InProgress)?.data?.let {
                        AddressProgress(
                            progress = it,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(
                                    end = 16.dp,
                                    bottom = 16.dp
                                )
                        )
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class, FlowPreview::class, ExperimentalCoroutinesApi::class)
    @Composable
    private fun Navigation() {
        val navController = rememberNavController()
        NavHost(
            navController, startDestination = Screen.meteoriteList.route,
            Modifier.fillMaxSize()
        ) {
            composable(Screen.meteoriteList.route) {
                ListScreenNavigation(
                    listViewModel
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

        NavigateToMeteorite(navController)

    }

    @Composable
    private fun NavigateToMeteorite(navController: NavHostController) {
        val navigateToDetail = listViewModel.navigateToMeteoriteDetail.collectAsState().value
        if (navigateToDetail != null) {
            navController.navigate("${Screen.meteoriteDetail.route}/${navigateToDetail.id}")
            listViewModel.onItemClicked(null)
        }
    }
}
