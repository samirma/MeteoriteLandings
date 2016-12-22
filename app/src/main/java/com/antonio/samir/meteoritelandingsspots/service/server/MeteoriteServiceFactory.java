package com.antonio.samir.meteoritelandingsspots.service.server;


import android.app.Activity;

/**
 * Create MeteoriteService used by UI to recover meteorites
 * MeteoriteService prevents the UI to know where the meteorites came from
 */
public class MeteoriteServiceFactory {

    public static MeteoriteService getMeteoriteService(final Activity context) {

        final MeteoriteService server = new MeteoriteNasaService(context);

        return server;
    }

}
