package com.antonio.samir.meteoritelandingsspots.features

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.service.monetization.MonetizationInterface
import org.koin.android.ext.android.inject

class MeteoriteMainEntryPointActivity : AppCompatActivity() {

    private val appBarConfiguration = AppBarConfiguration(setOf(R.id.meteoriteListFragment))

    val monetization: MonetizationInterface by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteorite_list)
        configureActionBar()

        monetization.start(lifecycleScope)

    }

    private fun configureActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navController = getNavController()

        navController.setGraph(R.navigation.nav_main, intent.extras)

        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    private fun getNavController(): NavController =
        (supportFragmentManager.findFragmentById(R.id.main_fragment_container_view)
                as NavHostFragment).navController

    override fun onSupportNavigateUp(): Boolean {
        return getNavController().navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
