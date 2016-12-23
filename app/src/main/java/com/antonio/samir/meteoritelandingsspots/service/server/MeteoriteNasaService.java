package com.antonio.samir.meteoritelandingsspots.service.server;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.MeteoriteNasaAsyncTaskService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

class MeteoriteNasaService implements MeteoriteService, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CURSOR_LOADER_ID = 1;

    private static final String TAG = MeteoriteNasaService.class.getSimpleName();
    private final NasaService nasaService;
    private final LoaderManager mLoaderManager;
    private Activity mActivity;
    private MeteoriteServiceDelegate mDelegate;
    private boolean firstAttempt;


    public MeteoriteNasaService(final Activity activity) {
        mActivity = activity;
        nasaService = NasaServiceFactory.getNasaService(mActivity);

        mLoaderManager = mActivity.getLoaderManager();

        firstAttempt = true;

    }

    @Override
    public void getMeteorites(MeteoriteServiceDelegate delegate) {
        mDelegate = delegate;

        final boolean hasNetWork = NetworkUtil.hasConnectivity(mActivity);
        if (hasNetWork) {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoaderManager.initLoader(CURSOR_LOADER_ID, null, MeteoriteNasaService.this);
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
        mLoaderManager.destroyLoader(CURSOR_LOADER_ID);
    }

    @Override
    public Cursor getMeteoriteById(String meteoriteId) {

        final Uri url = MeteoriteProvider.Meteorites.withId(meteoriteId);

        final Cursor cursor = mActivity.getContentResolver().query(url,
                new String[]{
                        MeteoriteColumns.ID
                        , MeteoriteColumns.NAME
                        , MeteoriteColumns.YEAR
                        , MeteoriteColumns.ADDRESS
                        , MeteoriteColumns.RECLONG
                        , MeteoriteColumns.RECLAT},
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
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
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

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDelegate.reReseted();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mDelegate.onPreExecute();
        // This narrows the return to only the stocks that are most current.
        return new CursorLoader(mActivity, MeteoriteProvider.Meteorites.LISTS,
                new String[]{MeteoriteColumns.ID, MeteoriteColumns.NAME, MeteoriteColumns.YEAR
                        , MeteoriteColumns.ADDRESS
                        , MeteoriteColumns.RECLONG
                        , MeteoriteColumns.RECLAT},
                null,
                null,
                null);
    }

}
