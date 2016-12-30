package com.antonio.samir.meteoritelandingsspots.service.server;


import android.content.Context;

/**
 * Create MeteoriteService used by UI to recover meteorites
 * MeteoriteService prevents the UI to know where the meteorites came from
 */
public class MeteoriteServiceFactory {

    public static MeteoriteService getMeteoriteService(final Context context) {

        final MeteoriteService server = new MeteoriteNasaService(context);

        return server;
    }

}
