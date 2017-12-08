package com.antonio.samir.meteoritelandingsspots.presenter;


import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;

public interface MeteoriteListView {
    Context getContext();

    void unableToFetch();

    void error(String s);

    void hideList();

    GPSTracker.GPSTrackerDelegate getGPSDelegate();

    void meteoriteLoadingStarted();

    void meteoriteLoadingStopped();
}
