package com.antonio.samir.meteoritelandingsspots.util.analytics

import android.content.Context


interface Analytics {

    fun start(context: Context)

    fun logEvent(category: String, event: String)

}
