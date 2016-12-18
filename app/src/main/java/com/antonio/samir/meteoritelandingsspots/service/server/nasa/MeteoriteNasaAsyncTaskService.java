package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import android.os.AsyncTask;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerResult;

import java.util.List;

public class MeteoriteNasaAsyncTaskService extends AsyncTask<Void, Void, MeteoriteServerResult> {

    private NasaService nasaService;

    public MeteoriteNasaAsyncTaskService(NasaService nasaService) {
        this.nasaService = nasaService;
    }

    @Override
    protected MeteoriteServerResult doInBackground(Void... params) {

        final MeteoriteServerResult meteoriteServerResult = new MeteoriteServerResult();

        final List<Meteorite> meteorites;
        try {
            meteorites = nasaService.getMeteorites();
            meteoriteServerResult.setMeteorites(meteorites);
        } catch (MeteoriteServerException e) {
            meteoriteServerResult.setException(e);
        }

        return meteoriteServerResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(final MeteoriteServerResult result) {


    }

}
