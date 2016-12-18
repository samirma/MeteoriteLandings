package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.R;

public class NasaServiceFactory {
    private static NasaService nasaService;

    public static void setNasaService(NasaService nasaService) {
        NasaServiceFactory.nasaService = nasaService;
    }

    public static NasaService getNasaService(Context context) {

        final NasaService service;

        if (nasaService != null) {
            service = nasaService;
        } else {
            service = new NasaServiceImpl();
        }

        return service;
    }
}
