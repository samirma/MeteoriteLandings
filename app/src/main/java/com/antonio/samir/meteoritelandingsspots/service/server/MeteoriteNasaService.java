package com.antonio.samir.meteoritelandingsspots.service.server;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
    public static final String[] PROJECTION = {ID, NAME, YEAR
            , RECLONG
            , RECLAT};
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
    public void getMeteorites(MeteoriteServiceDelegate delegate, AppCompatActivity mActivity) {
        mDelegate = delegate;

        mLoaderManager = mActivity.getSupportLoaderManager();

        final boolean hasNetWork = NetworkUtil.hasConnectivity(mContext);
        if (hasNetWork) {
            mActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    final android.support.v4.content.Loader<Object> loader = mLoaderManager.getLoader(CURSOR_LOADER_ID);
                    if (loader == null) {
                        mLoaderManager.initLoader(CURSOR_LOADER_ID, null, MeteoriteNasaService.this);
                    } else {
                        final android.support.v4.content.Loader<Cursor> cursorLoader = mLoaderManager.restartLoader(CURSOR_LOADER_ID, null, MeteoriteNasaService.this);
                        cursorLoader.startLoading();
                        cursorLoader.forceLoad();
                        cursorLoader.getContext().getContentResolver().notifyChange(MeteoriteProvider.Meteorites.LISTS, null);

                    }
                }

            });
        } else {
            delegate.unableToFetch();
        }

    }


    @Override
    public void remove() {
        mLoaderManager.destroyLoader(CURSOR_LOADER_ID);
    }

    @Override
    public Cursor getMeteoriteById(String meteoriteId) {

        final Uri url = MeteoriteProvider.Meteorites.withId(meteoriteId);

        final ContentResolver contentResolver = mContext.getContentResolver();
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

        final CursorLoader cursorLoader = getMeteoriteListCursorLoader();
        return cursorLoader;
    }

    @NonNull
    @Override
    public CursorLoader getMeteoriteListCursorLoader() {

        String sortOrder = getOrderString();

        return new CursorLoader(mContext, MeteoriteProvider.Meteorites.LISTS,
                PROJECTION,
                null,
                null,
                sortOrder);
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

    @Override
    public String[] getProjection() {
        return PROJECTION;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {

        if (data == null || data.getCount() < 1) {
            Log.i(TAG, "No data found starting to recovery service");
            if (firstAttempt) {
                final MeteoriteNasaAsyncTaskService taskService = new MeteoriteNasaAsyncTaskService(nasaService, mContext) {
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
            }
        } else {
            Log.i(TAG, String.format("Data count %s", data.getCount()));
            mDelegate.setCursor(data);
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {

    }


}
