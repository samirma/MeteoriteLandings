package com.antonio.samir.meteoritelandingsspots.features.list.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.antonio.samir.meteoritelandingsspots.R
import kotlinx.android.synthetic.main.activity_meteorite_list.*

class MeteoriteListMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meteorite_list)

        if (toolbarTB != null) {
            setSupportActionBar(toolbarTB)
        }

    }

}
