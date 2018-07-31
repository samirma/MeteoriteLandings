package com.antonio.samir.meteoritelandingsspots.ui.activity

import android.content.Intent
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteListMainActivity.Companion.ITEM_SELECTED
import com.antonio.samir.meteoritelandingsspots.ui.fragments.MeteoriteDetailFragment

class MeteoriteDetailActivity : AppCompatActivity() {

    private var selectedMeteorite: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteorite_detail)

        val isLandscape = resources.configuration.orientation == ORIENTATION_LANDSCAPE

        selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState)

        if (selectedMeteorite != null) {
            if (isLandscape) {
                //If MeteoriteDetailActivity is created on landscape so return to MeteoriteListMainActivity
                val intent = Intent(this, MeteoriteListMainActivity::class.java)
                intent.putExtra(ITEM_SELECTED, selectedMeteorite)
                startActivity(intent)
            } else {
                //If MeteoriteDetailActivity is created on portrait so load the fragment
                val meteoriteDetailFragment = MeteoriteDetailFragment.newInstance(selectedMeteorite!!)
                val fm = supportFragmentManager
                val fragmentTransaction = fm.beginTransaction()
                fragmentTransaction.add(R.id.frag_detail, meteoriteDetailFragment)
                fragmentTransaction.commit()
            }
        }

    }

    private fun getPreviousSelectedMeteorite(savedInstanceState: Bundle?): String? {
        val extras = intent.extras
        var meteorite: String? = null
        if (extras != null) {
            meteorite = extras.getString(ITEM_SELECTED)
        } else if (meteorite == null && savedInstanceState != null) {
            meteorite = savedInstanceState.getString(ITEM_SELECTED)
        }
        return meteorite
    }

    /**
     * Activity cicle life saving state
     * @param savedInstanceState
     */
    public override fun onSaveInstanceState(savedInstanceState: Bundle) {

        //Saving the selected meteorite
        savedInstanceState.putString(ITEM_SELECTED, selectedMeteorite)

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState)
    }

}
