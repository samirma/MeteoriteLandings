package com.antonio.samir.meteoritelandingsspots.service.server;

import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.service.server.nasa.MeteoriteNasaAsyncTaskService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

public class MeteoriteNasaService  implements MeteoriteService {

    private static final String TAG = MeteoriteNasaService.class.getSimpleName();
    private final NasaService nasaService;
    private Context context;

    public MeteoriteNasaService(Context context) {
        this.context = context;
        nasaService = NasaServiceFactory.getNasaService(context);
    }

    @Override
    public void getMeteorites() {
        final MeteoriteNasaAsyncTaskService taskService = new MeteoriteNasaAsyncTaskService(nasaService);
        taskService.execute();
    }

    @Override
    public boolean isDated() {
        return false;
    }


}
