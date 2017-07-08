package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.ResultUtil;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerResult;

import java.util.ArrayList;
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
            final ContentResolver contentResolver = mContext.getContentResolver();
            final ArrayList operations = ResultUtil.quoteJsonToContentVals(meteorites, contentResolver);
            contentResolver.delete(MeteoriteProvider.Meteorites.LISTS, null, null);
            contentResolver.applyBatch(MeteoriteProvider.AUTHORITY, operations);
        } catch (MeteoriteServerException | RemoteException | OperationApplicationException e) {
            Log.e(MeteoriteNasaAsyncTaskService.class.getSimpleName(), e.getMessage(), e);
        }
    }

}
