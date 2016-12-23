package com.antonio.samir.meteoritelandingsspots;


import android.content.Context;

import com.facebook.stetho.Stetho;

public class Application extends android.app.Application {

    private static Context applicationContext;

    public static Context getContext() {
        return applicationContext;
    }

    public void onCreate() {
        super.onCreate();
        //chrome://inspect
        Stetho.initializeWithDefaults(this);
        applicationContext = getApplicationContext();
    }
}
