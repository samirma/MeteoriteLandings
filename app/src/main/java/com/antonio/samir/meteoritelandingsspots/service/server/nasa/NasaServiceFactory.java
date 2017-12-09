package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

public class NasaServiceFactory {
    private static NasaService nasaService;

    public static NasaService getNasaService() {

        final NasaService service;

        if (nasaService != null) {
            service = nasaService;
        } else {
            service = new NasaServiceImpl();
        }

        return service;
    }
}
