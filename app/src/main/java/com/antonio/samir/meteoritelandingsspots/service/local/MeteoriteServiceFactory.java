package com.antonio.samir.meteoritelandingsspots.service.local;


import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;

/**
 * Create MeteoriteService used by UI to recover meteorites
 * MeteoriteService prevents the UI to know where the meteorites came from
 */
public class MeteoriteServiceFactory {

    public static MeteoriteService getMeteoriteService(final Context context, GPSTracker gpsTracker) {

        final MeteoriteService server = new MeteoriteNasaService(context, gpsTracker);

        return server;
    }

}
