package com.antonio.samir.meteoritelandingsspots.features

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.antonio.samir.meteoritelandingsspots.R

class MeteoriteMainEntryPointActivity : AppCompatActivity(R.layout.activity_meteorite_list) {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteorite_list)
        configureActionBar()
    }

    private fun configureActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val appBarConfiguration = AppBarConfiguration(
                setOf(R.id.meteoriteListFragment)
        )

        val navController = getNavController()

        navController.setGraph(R.navigation.nav_main, intent.extras)

        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    private fun getNavController() =
            (supportFragmentManager.findFragmentById(R.id.main_fragment_container_view)
                    as NavHostFragment).navController

}
