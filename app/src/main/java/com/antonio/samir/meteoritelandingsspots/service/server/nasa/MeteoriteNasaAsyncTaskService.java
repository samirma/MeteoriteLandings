package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepository;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.service.server.AddressService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerResult;

import java.util.List;

public class MeteoriteNasaAsyncTaskService extends AsyncTask<Void, Void, MeteoriteServerResult> {

    private final Context mContext;
    private NasaService mNasaService;

    public MeteoriteNasaAsyncTaskService(NasaService nasaService, Context context) {
        mNasaService = nasaService;
        mContext = context;
    }

    @Override
    protected MeteoriteServerResult doInBackground(Void... params) {

        final MeteoriteServerResult meteoriteServerResult = new MeteoriteServerResult();

        final List<Meteorite> meteorites;
        try {
            meteorites = mNasaService.getMeteorites();
            meteoriteServerResult.setMeteorites(meteorites);
        } catch (MeteoriteServerException e) {
            meteoriteServerResult.setException(e);
        }

        saveMeteorites(meteoriteServerResult);

        return meteoriteServerResult;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    private void saveMeteorites(MeteoriteServerResult result) {
        try {
            final List<Meteorite> meteorites = result.getMeteorites();

            final MeteoriteDao meteoriteDao = new MeteoriteRepository(mContext).getAppDatabase().meteoriteDao();

            meteoriteDao.insertAll(meteorites);

            AddressService addressService = new AddressService();

            for (Meteorite met: meteorites) {
                addressService.recoverAddress(met, met.getReclat(), met.getReclong());
            }

        } catch (MeteoriteServerException e) {
            Log.e(MeteoriteNasaAsyncTaskService.class.getSimpleName(), e.getMessage(), e);
        }
    }

}
