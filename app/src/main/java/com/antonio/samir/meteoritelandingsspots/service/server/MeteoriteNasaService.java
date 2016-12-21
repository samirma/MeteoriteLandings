package com.antonio.samir.meteoritelandingsspots.service.server;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.MeteoriteNasaAsyncTaskService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;

public class MeteoriteNasaService implements MeteoriteService, LoaderManager.LoaderCallbacks<Cursor> {
    private static final int CURSOR_LOADER_ID = 1;

    private static final String TAG = MeteoriteNasaService.class.getSimpleName();
    private final NasaService nasaService;
    private final LoaderManager mLoaderManager;
    private Context mContext;
    private MeteoriteServiceDelegate mDelegate;
    private boolean firstAttempt;


    public MeteoriteNasaService(final Context context) {
        mContext = context;
        nasaService = NasaServiceFactory.getNasaService(context);

        mLoaderManager = ((Activity) mContext).getLoaderManager();

        firstAttempt = true;

    }

    @Override
    public void getMeteorites(MeteoriteServiceDelegate delegate) {
        mDelegate = delegate;
        mLoaderManager.initLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public boolean isDated() {
        return false;
    }

    // LoaderManager.LoaderCallbacks<Cursor> implemendation
    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        if (data == null || data.getCount() < 1) {
            if (firstAttempt) {
                final MeteoriteNasaAsyncTaskService taskService = new MeteoriteNasaAsyncTaskService(nasaService, mContext) {
                    @Override
                    protected void onPostExecute(MeteoriteServerResult result) {
                        super.onPostExecute(result);
                        mLoaderManager.restartLoader(CURSOR_LOADER_ID, null, MeteoriteNasaService.this);
                    }
                };
                firstAttempt = false;
                taskService.execute();
            } else {
                //???
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
        return new CursorLoader(mContext, MeteoriteProvider.Meteorites.LISTS,
                new String[]{MeteoriteColumns.ID, MeteoriteColumns.NAME, MeteoriteColumns.YEAR
                        , MeteoriteColumns.RECLONG
                        , MeteoriteColumns.RECLAT},
                null,
                null,
                null);
    }

}
