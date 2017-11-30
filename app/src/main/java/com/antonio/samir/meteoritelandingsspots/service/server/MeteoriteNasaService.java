package com.antonio.samir.meteoritelandingsspots.service.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.MeteoriteNasaAsyncTaskService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

import java.util.List;

class MeteoriteNasaService implements MeteoriteService {

    private static final String TAG = MeteoriteNasaService.class.getSimpleName();
    private final NasaService nasaService;
    private final GPSTracker gpsTracker;
    private Context mContext;
    private MeteoriteServiceDelegate mDelegate;
    private boolean firstAttempt;


    public MeteoriteNasaService(final Context context) {
        mContext = context;
        nasaService = NasaServiceFactory.getNasaService(mContext);

        firstAttempt = true;

        gpsTracker = new GPSTracker(mContext);

    }

    @Override
    public void getMeteorites(final MeteoriteServiceDelegate delegate) {
        mDelegate = delegate;

        new AsyncTask<Void, Void, List<Meteorite>>() {
            private boolean mHasNetWork;

            @Override
            protected void onPreExecute() {
                mHasNetWork = NetworkUtil.hasConnectivity(mContext);
            }

            @Override
            protected List<Meteorite> doInBackground(final Void... voids) {

                List<Meteorite> list;

                if (mHasNetWork) {
                    //Return meteories
                    final MeteoriteDao meteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(mContext);
                    list = meteoriteDao.retrieve(getOrderString());

                } else {
                    list = null;
                }

                return list;
            }

            @Override
            protected void onPostExecute(final List<Meteorite> meteorites) {
                super.onPostExecute(meteorites);
                if (meteorites == null && !mHasNetWork) {
                    delegate.unableToFetch();
                } else {
                    onMeteoritesLoaded(meteorites);
                }
            }
        }.execute();

    }

    public String getOrderString() {
        String sortOrder = null;

        final boolean gpsEnabled = gpsTracker.isGPSEnabled();

        try {
            if (gpsEnabled && gpsTracker.getLocation() != null) {
                final double latitude = gpsTracker.getLatitude();
                final double longitude = gpsTracker.getLongitude();
                sortOrder = String.format("ABS(reclat - %s ) + ABS(reclong - %s) ASC", latitude, longitude);
            }
        } catch (Exception e) {
            Log.e(TAG, "GPS failed", e);
        }
        return sortOrder;
    }


    public void onMeteoritesLoaded(final List<Meteorite> meteorites) {

        if (meteorites != null && meteorites.isEmpty()) {
            Log.i(TAG, "No data found starting to recovery service");
            if (firstAttempt) {
                //Recover meteories from the server
                final MeteoriteNasaAsyncTaskService taskService = new MeteoriteNasaAsyncTaskService(nasaService, mContext) {
                    @Override
                    protected void onPostExecute(MeteoriteServerResult result) {
                        getMeteorites(mDelegate);
                    }
                };
                firstAttempt = false;
                taskService.execute();
            }
        } else {
            Log.i(TAG, String.format("Data count %s", meteorites.size()));
            mDelegate.setMeteorites(meteorites);
        }

    }


}
