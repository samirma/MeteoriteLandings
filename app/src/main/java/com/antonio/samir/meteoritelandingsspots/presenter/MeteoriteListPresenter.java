package com.antonio.samir.meteoritelandingsspots.presenter;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceDelegate;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

/**
 * Presenter layer responsible for manage the interactions between the activity and the services
 */
public class MeteoriteListPresenter implements MeteoriteServiceDelegate {

    private static final String TAG = MeteoriteListPresenter.class.getSimpleName();
    private final Context mContext;
    private final MeteoriteService meteoriteFetchService;
    private MeteoriteListView mView;

    public MeteoriteListPresenter(MeteoriteListView view) {
        this.mView = view;

        mContext = view.getContext();

        meteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(mContext);

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
        meteoriteFetchService.getMeteorites(this, (AppCompatActivity) mView.getContext());
    }

    public void removeView(final MeteoriteListView view) {
        meteoriteFetchService.remove();
    }

    //MeteoriteServiceDelegate Implemendation
    @Override
    public void onPreExecute() {
        mView.onPreExecute();
    }

    @Override
    public void setCursor(Cursor data) {
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
    public void fail(MeteoriteServerException e) {
        Log.e(TAG, e.getMessage(), e);
        mView.error(e.getMessage());
    }

    @Override
    public void reReseted() {
        mView.clearList();
    }

    @Override
    public void unableToFetch() {
        mView.unableToFetch();
    }
}
