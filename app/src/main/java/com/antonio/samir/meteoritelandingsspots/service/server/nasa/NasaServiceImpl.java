package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.util.analytics.AnalyticsUtil;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NasaServiceImpl implements NasaService {

    private static final String NASA_SERVICE = "NasaService";
    private final NasaServerEndPoint service;

    public NasaServiceImpl(){
        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(NasaServerEndPoint.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(NasaServerEndPoint.class);

    }

    @Override
    public List<Meteorite> getMeteorites() throws MeteoriteServerException {

        final Call<List<Meteorite>> publicMeteorites = service.getPublicMeteorites();

        final List<Meteorite> meteorites;
        try {
            meteorites = publicMeteorites.execute().body();
            AnalyticsUtil.logEvent(NASA_SERVICE, "Meteorites recovered from Nasa server");
        } catch (IOException e) {
            AnalyticsUtil.logEvent(NASA_SERVICE, "Failed to recover data from Nasa server");
            throw new MeteoriteServerException(e);
        }

        return meteorites;
    }

}
