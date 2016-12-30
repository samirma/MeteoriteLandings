package com.antonio.samir.meteoritelandingsspots.util.analytics;

import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;


public class GoogleAnalyticsUtil implements Analytics {

    private Tracker mTracker;

    @Override
    public void start(Context context) {

        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }

        mTracker.enableAutoActivityTracking(true);


    }

    @Override
    public void logEvent(String category, String event) {

        final Map<String, String> build = new HitBuilders.EventBuilder()
                .setAction(event)
                .setCategory(category)
                .build();
        mTracker.send(build);
    }

}

