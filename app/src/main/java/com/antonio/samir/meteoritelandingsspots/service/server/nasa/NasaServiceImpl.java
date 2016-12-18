package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NasaServiceImpl implements NasaService {

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
        } catch (IOException e) {
            throw new MeteoriteServerException(e);
        }

        return meteorites;
    }

}
