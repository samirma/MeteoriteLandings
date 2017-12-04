package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import android.os.AsyncTask;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.service.server.AddressService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerResult;

import java.util.List;

public class MeteoriteNasaAsyncTaskService extends AsyncTask<Void, Void, MeteoriteServerResult> {

    public static final String TAG = MeteoriteNasaAsyncTaskService.class.getSimpleName();
    private NasaService mNasaService;
    private MeteoriteDao mMeteoriteDao;

    public MeteoriteNasaAsyncTaskService(NasaService nasaService, MeteoriteDao meteoriteDao) {
        mNasaService = nasaService;
        mMeteoriteDao = meteoriteDao;
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

            mMeteoriteDao.insertAll(meteorites);

            final AddressService addressService = new AddressService();

            new Thread("AddressService") {
                @Override
                public void run() {
                    try {
                        for (Meteorite met: meteorites) {
                            addressService.recoverAddress(met, met.getReclat(), met.getReclong());
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Fail to retrieve addresss", e);
                    }
                }
            }.start();

        } catch (MeteoriteServerException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
