package com.antonio.samir.meteoritelandingsspots.service;


import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;

import java.util.ArrayList;
import java.util.List;


/**
 * Class to help
 */
public class MeteoriteServiceTest implements NasaService {


    private MeteoriteServerException exception;

    @Override
    public List<Meteorite> getMeteorites() throws MeteoriteServerException {

        if (exception != null) {
            throw exception;
        }

        List<Meteorite> list = new ArrayList<>();
        final Meteorite meteorite = new Meteorite();
        meteorite.setId("1");

        meteorite.setMass("3");

        meteorite.setNametype("3");

        meteorite.setRecclass("3");

        meteorite.setName("3");

        meteorite.setFall("3");

        meteorite.setYear("1983");

        meteorite.setReclong("3");

        list.add(meteorite);
        return list;
    }

    public void setError(MeteoriteServerException error) {
        this.exception = error;
    }
}