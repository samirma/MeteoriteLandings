package com.antonio.samir.meteoritelandingsspots.features

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.detail.ui.MeteoriteDetailFragment
import kotlinx.android.synthetic.main.activity_meteorite_list.*

class MeteoriteMainEntryPointActivity : AppCompatActivity() {

    private var selectedMeteorite: String? = null

    private var meteoriteDetailFragment: MeteoriteDetailFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteorite_list)
    }

    fun selectMeteoriteLandscape(meteorite: String?) {

        fragment?.visibility = View.VISIBLE

        var fragmentTransaction = supportFragmentManager.beginTransaction()

        fragmentTransaction = fragmentTransaction.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit)

        meteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite!!)
        fragmentTransaction.replace(R.id.fragment, meteoriteDetailFragment!!)
        fragmentTransaction.commit()

        selectedMeteorite = meteorite

    }

}
