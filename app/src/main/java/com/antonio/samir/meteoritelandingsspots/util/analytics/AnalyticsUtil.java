package com.antonio.samir.meteoritelandingsspots.util.analytics;

import android.content.Context;

/**
 * Created by samir on 27/02/15.
 */
public class AnalyticsUtil {

    private static Analytics analytics;

    static {
        analytics = new GoogleAnalyticsUtil();
    }

    public static void start(Context context) {
        analytics.start(context);
    }

    public static void logEvent(String category, String event) {
        analytics.logEvent(category, event);
    }

}

