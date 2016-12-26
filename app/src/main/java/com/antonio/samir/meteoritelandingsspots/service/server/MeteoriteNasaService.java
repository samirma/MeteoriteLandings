package com.antonio.samir.meteoritelandingsspots.service.server;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.MeteoriteNasaAsyncTaskService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.ID;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.MASS;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAME;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAMETYPE;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECCLASS;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECLAT;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECLONG;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.YEAR;

class MeteoriteNasaService implements MeteoriteService, android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CURSOR_LOADER_ID = 1;

    private static final String TAG = MeteoriteNasaService.class.getSimpleName();
    private final NasaService nasaService;
    private final GPSTracker gpsTracker;
    private android.support.v4.app.LoaderManager mLoaderManager;
    private AppCompatActivity mActivity;
    private MeteoriteServiceDelegate mDelegate;
    private boolean firstAttempt;


    public MeteoriteNasaService(final AppCompatActivity activity) {
        mActivity = activity;
        nasaService = NasaServiceFactory.getNasaService(mActivity);

        firstAttempt = true;

        gpsTracker = new GPSTracker(mActivity);

    }

    @Override
    public void getMeteorites(MeteoriteServiceDelegate delegate) {
        mDelegate = delegate;

        mLoaderManager = mActivity.getSupportLoaderManager();

        final boolean hasNetWork = NetworkUtil.hasConnectivity(mActivity);
        if (hasNetWork) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    final android.support.v4.content.Loader<Object> loader = mLoaderManager.getLoader(CURSOR_LOADER_ID);
                    if (loader == null) {
                        final android.support.v4.content.Loader<Cursor> cursorLoader = mLoaderManager.initLoader(CURSOR_LOADER_ID, null, MeteoriteNasaService.this);
                        Log.e(TAG, String.format("%s %s %s", cursorLoader.isStarted(), cursorLoader.isAbandoned(), cursorLoader.isReset()));
                    } else {
                        final android.support.v4.content.Loader<Cursor> cursorLoader = mLoaderManager.restartLoader(CURSOR_LOADER_ID, null, MeteoriteNasaService.this);
                        cursorLoader.startLoading();
                        cursorLoader.forceLoad();
                        cursorLoader.getContext().getContentResolver().notifyChange(MeteoriteProvider.Meteorites.LISTS, null);
                        Log.e(TAG, String.format("%s %s %s", cursorLoader.isStarted(), cursorLoader.isAbandoned(), cursorLoader.isReset()));

                    }
                }

            });
        } else {
            delegate.unableToFetch();
        }

    }

    @Override
    public boolean isDated() {
        return false;
    }

    @Override
    public void remove() {
        //mLoaderManager.destroyLoader(CURSOR_LOADER_ID);
    }

    @Override
    public Cursor getMeteoriteById(String meteoriteId) {

        final Uri url = MeteoriteProvider.Meteorites.withId(meteoriteId);

        final ContentResolver contentResolver = mActivity.getContentResolver();
        final Cursor cursor = contentResolver.query(url,
                new String[]{
                        ID
                        , NAME
                        , YEAR
                        , NAMETYPE
                        , RECCLASS
                        , MASS
                        , RECLONG
                        , RECLAT},
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }


    // LoaderManager.LoaderCallbacks<Cursor> implemendation
    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mDelegate.onPreExecute();
        // This narrows the return to only the stocks that are most current.

        String sortOrder = null;

        final boolean gpsEnabled = gpsTracker.isGPSEnabled();

        if (gpsEnabled && gpsTracker.getLocation() != null) {
            final double latitude = gpsTracker.getLatitude();
            final double longitude = gpsTracker.getLongitude();
            sortOrder = String.format("ABS(reclat - %s ) + ABS(reclong - %s) ASC", latitude, longitude);
        }

        final CursorLoader cursorLoader = new CursorLoader(mActivity, MeteoriteProvider.Meteorites.LISTS,
                new String[]{ID, NAME, YEAR
                        , RECLONG
                        , RECLAT},
                null,
                null,
                sortOrder);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        {
            if (data == null || data.getCount() < 1) {
                Log.i(TAG, "No data found starting to recovery service");
                if (firstAttempt) {
                    final MeteoriteNasaAsyncTaskService taskService = new MeteoriteNasaAsyncTaskService(nasaService, mActivity) {
                        @Override
                        protected void onPostExecute(MeteoriteServerResult result) {
                            super.onPostExecute(result);
                            Log.i(TAG, "Recovery service done");
                            //Reloading LoaderManager in order to get the data from data base
                            mLoaderManager.restartLoader(CURSOR_LOADER_ID, null, MeteoriteNasaService.this);
                        }
                    };
                    firstAttempt = false;
                    taskService.execute();
                } else {
                    mDelegate.unableToFetch();
                }
            } else {
                Log.i(TAG, String.format("Data count %s", data.getCount()));
                mDelegate.setCursor(data);
            }
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


}
