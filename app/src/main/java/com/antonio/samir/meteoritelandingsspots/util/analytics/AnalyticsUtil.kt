package com.antonio.samir.meteoritelandingsspots.util.analytics

import android.content.Context


object AnalyticsUtil {

    private val analytics: Analytics

    init {
        analytics = GoogleAnalyticsUtil()
    }

    fun start(context: Context) {
        analytics.start(context)
    }

    fun logEvent(category: String, event: String) {
        analytics.logEvent(category, event)
    }

}

