package com.antonio.samir.meteoritelandingsspots;

import com.facebook.stetho.Stetho;

public class ApplicationDebug extends com.antonio.samir.meteoritelandingsspots.Application {

    public void onCreate() {
        super.onCreate();

        //chrome://inspect
        Stetho.initializeWithDefaults(this);

    }

}
