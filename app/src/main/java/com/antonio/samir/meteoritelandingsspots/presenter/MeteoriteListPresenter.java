package com.antonio.samir.meteoritelandingsspots.presenter;


import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

/**
 * Presenter layer responsible for manage the interactions between the activity and the services
 */
public class MeteoriteListPresenter implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = MeteoriteListPresenter.class.getSimpleName();
    private static final int CURSOR_LOADER_ID = 1;
    private final Context mContext;
    private final MeteoriteService meteoriteFetchService;
    private final LoaderManager loaderManager;
    private MeteoriteListView mView;

    public MeteoriteListPresenter(MeteoriteListView view) {
        this.mView = view;

        mContext = view.getContext();

        meteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(mContext);

        loaderManager = ((Activity) mContext).getLoaderManager();

        loaderManager.initLoader(CURSOR_LOADER_ID, null, this);

    }

    /**
     * Set the initial page and start meteorites fetching
     */
    public void startToRecoverMeteorites() {

        mView.clearList();

        recoverPage();

    }

    /**
     * Fetch the meteorites
     */
    private void recoverPage() {

        final boolean hasNetWork = NetworkUtil.hasConnectivity(mContext);

        if (hasNetWork) {

            recoverMeteorites();

        } else {
            //If the is no Internet view should be notified
            mView.unableToFetch();
        }

    }

    private void recoverMeteorites() {
        meteoriteFetchService.getMeteorites();
    }


    // LoaderManager.LoaderCallbacks<Cursor> implemendation

    @Override
    public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
        final boolean isNotEmpty = (data != null && (data.getCount() > 0));
        if (isNotEmpty) {
            mView.setMeteorites(data);
            mView.showList();
            final boolean dated = meteoriteFetchService.isDated();
            if (dated) {
                mView.showDatedMessage();
            } else {
                mView.hideDatedMessage();
            }
        } else {
            mView.hideList();
            mView.hideDatedMessage();
        }

        if (!NetworkUtil.hasConnectivity(mContext)) {
            mView.unableToFetch();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mView.clearList();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
