package com.antonio.samir.meteoritelandingsspots;


import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.util.analytics.AnalyticsUtil;

public class Application extends android.app.Application {

    private static Context applicationContext;

    public static Context getContext() {
        return applicationContext;
    }

    public void onCreate() {
        super.onCreate();

        applicationContext = getApplicationContext();

        AnalyticsUtil.start(this);

    }
}
