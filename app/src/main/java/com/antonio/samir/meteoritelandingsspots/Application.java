package com.antonio.samir.meteoritelandingsspots;


import com.facebook.stetho.Stetho;

public class Application extends android.app.Application {

    public void onCreate() {
        super.onCreate();
        //chrome://inspect
        Stetho.initializeWithDefaults(this);
    }

}
