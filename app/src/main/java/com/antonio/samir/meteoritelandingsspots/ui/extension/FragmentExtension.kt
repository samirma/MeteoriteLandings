package com.antonio.samir.meteoritelandingsspots.ui.extension

import android.content.res.Configuration
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.antonio.samir.meteoritelandingsspots.R
import com.google.android.material.appbar.AppBarLayout

fun Fragment.hideActionBar() {
    with(requireActivity()) {
        val appBarLayoutFlowMain = findViewById<AppBarLayout>(R.id.app_bar_flow)
        appBarLayoutFlowMain?.visibility = View.GONE
    }
}

fun Fragment.showActionBar(title: CharSequence? = null) {
    with(requireActivity()) {
        val textActionBarTitle = findViewById<TextView>(R.id.toolbar_title)
        val appBarLayoutFlowMain = findViewById<AppBarLayout>(R.id.app_bar_flow)
        title?.let {
            textActionBarTitle?.text = it
        }
        appBarLayoutFlowMain?.visibility = View.VISIBLE
    }
}

fun Fragment.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}