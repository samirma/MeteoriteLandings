package com.antonio.samir.meteoritelandingsspots.service.server;

import android.content.Context;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.MeteoriteNasaAsyncTaskService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

import java.util.List;

class MeteoriteNasaService implements MeteoriteService {

    private static final int CURSOR_LOADER_ID = 1;
    private static final String TAG = MeteoriteNasaService.class.getSimpleName();
    private final NasaService nasaService;
    private final GPSTracker gpsTracker;
    private android.support.v4.app.LoaderManager mLoaderManager;
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
    public void getMeteorites(MeteoriteServiceDelegate delegate) {
        mDelegate = delegate;

        final boolean hasNetWork = NetworkUtil.hasConnectivity(mContext);
        if (hasNetWork) {
            //Return meteories
        } else {
            delegate.unableToFetch();
        }

    }


    @Override
    public void remove() {
        mLoaderManager.destroyLoader(CURSOR_LOADER_ID);
    }


    public String getOrderString() {
        String sortOrder = null;

        final boolean gpsEnabled = gpsTracker.isGPSEnabled();

        if (gpsEnabled && gpsTracker.getLocation() != null) {
            final double latitude = gpsTracker.getLatitude();
            final double longitude = gpsTracker.getLongitude();
            sortOrder = String.format("ABS(reclat - %s ) + ABS(reclong - %s) ASC", latitude, longitude);
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
            mDelegate.setCursor(meteorites);
        }

    }


}
