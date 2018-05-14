package com.antonio.samir.meteoritelandingsspots.service.local


import android.content.Context

import com.antonio.samir.meteoritelandingsspots.util.GPSTracker

/**
 * Create MeteoriteService used by UI to recover meteorites
 * MeteoriteService prevents the UI to know where the meteorites came from
 */
object MeteoriteServiceFactory {

    fun getMeteoriteService(context: Context, gpsTracker: GPSTracker): MeteoriteService {

        return MeteoriteNasaService(context, gpsTracker)
    }

}
