package com.antonio.samir.meteoritelandingsspots.common.ui.extension

import android.content.res.Configuration
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.antonio.samir.meteoritelandingsspots.R
import com.google.android.material.appbar.AppBarLayout

fun Fragment.isLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}