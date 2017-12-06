package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import android.os.AsyncTask;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService;
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerResult;

import java.util.List;

public class MeteoriteNasaAsyncTaskService extends AsyncTask<Void, Void, MeteoriteServerResult> {

    public static final String TAG = MeteoriteNasaAsyncTaskService.class.getSimpleName();
    private final NasaService mNasaService;
    private final MeteoriteDao mMeteoriteDao;

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

            addressService.recoveryAddress();

        } catch (MeteoriteServerException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}
