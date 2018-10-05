package com.antonio.samir.meteoritelandingsspots


import com.antonio.samir.meteoritelandingsspots.util.analytics.AnalyticsUtil

open class Application : android.app.Application() {

    override fun onCreate() {
        super.onCreate()

        AnalyticsUtil.start(this)

    }
}
