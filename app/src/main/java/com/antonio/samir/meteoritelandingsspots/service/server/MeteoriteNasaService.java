package com.antonio.samir.meteoritelandingsspots.service.server;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

import java.util.List;

class MeteoriteNasaService implements MeteoriteService {

    private static final String TAG = MeteoriteNasaService.class.getSimpleName();
    private Context mContext;


    public MeteoriteNasaService(final Context context) {
        mContext = context;
    }

    @Override
    public void getMeteorites(final MeteoriteServiceDelegate delegate) {

        new deleteAsyncTask(delegate, mContext).execute();

    }


    private static class deleteAsyncTask extends AsyncTask<Void, Void, List<Meteorite>> {
        private final GPSTracker mGpsTracker;
        private MeteoriteServiceDelegate mDelegate;
        private Context mContext;

        private boolean mHasNetWork;

        public deleteAsyncTask(MeteoriteServiceDelegate delegate, Context mContext) {
            mDelegate = delegate;
            this.mContext = mContext;
            mGpsTracker = new GPSTracker(mContext);
        }

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
                mDelegate.unableToFetch();
            } else {
                Log.i(TAG, String.format("Data count %s", meteorites.size()));
                mDelegate.setMeteorites(meteorites);
            }
        }

        public String getOrderString() {
            String sortOrder = null;

            final boolean gpsEnabled = mGpsTracker.isGPSEnabled();

            try {
                if (gpsEnabled && mGpsTracker.getLocation() != null) {
                    final double latitude = mGpsTracker.getLatitude();
                    final double longitude = mGpsTracker.getLongitude();
                    sortOrder = String.format("ABS(reclat - %s ) + ABS(reclong - %s) ASC", latitude, longitude);
                }
            } catch (Exception e) {
                Log.e(TAG, "GPS failed", e);
            }
            return sortOrder;
        }

    }

}
