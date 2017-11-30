package com.antonio.samir.meteoritelandingsspots.presenter;


import android.content.Context;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceDelegate;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

import java.util.List;

/**
 * Presenter layer responsible for manage the interactions between the activity and the services
 */
public class MeteoriteListPresenter implements MeteoriteServiceDelegate {

    private static final String TAG = MeteoriteListPresenter.class.getSimpleName();
    private static MeteoriteListView mView;
    private final Context mContext;
    private final MeteoriteService meteoriteFetchService;

    public MeteoriteListPresenter(MeteoriteListView view) {
        mView = view;

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
        meteoriteFetchService.getMeteorites(this);
    }

    //MeteoriteServiceDelegate Implemendation
    @Override
    public void onPreExecute() {
        mView.onPreExecute();
    }

    @Override
    public void setMeteorites(final List<Meteorite> meteorites) {
        final boolean isNotEmpty = (meteorites != null && (meteorites.size() > 0));
        if (isNotEmpty) {
            mView.setMeteorites(meteorites);
            mView.showList();
        } else {
            mView.hideList();
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
    public void unableToFetch() {
        mView.unableToFetch();
    }
}
