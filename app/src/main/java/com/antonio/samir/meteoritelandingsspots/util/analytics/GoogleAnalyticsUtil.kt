package com.antonio.samir.meteoritelandingsspots.util.analytics

import android.content.Context

import com.antonio.samir.meteoritelandingsspots.R
import com.google.android.gms.analytics.GoogleAnalytics
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker


class GoogleAnalyticsUtil : Analytics {

    private var mTracker: Tracker? = null

    override fun start(context: Context) {

        if (mTracker == null) {
            val analytics = GoogleAnalytics.getInstance(context)
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker)
        }

        mTracker!!.enableAutoActivityTracking(true)


    }

    override fun logEvent(category: String, event: String) {

        val build = HitBuilders.EventBuilder()
                .setAction(event)
                .setCategory(category)
                .build()
        mTracker!!.send(build)
    }

}

