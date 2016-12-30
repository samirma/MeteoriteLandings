package com.antonio.samir.meteoritelandingsspots.util.analytics;

import android.content.Context;


public interface Analytics {

    void start(Context context);

    void logEvent(String category, String event);

}
