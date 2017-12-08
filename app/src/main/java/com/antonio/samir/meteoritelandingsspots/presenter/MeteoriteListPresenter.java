package com.antonio.samir.meteoritelandingsspots.presenter;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService;
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServiceFactory;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Presenter layer responsible for manage the interactions between the activity and the services
 */
public class MeteoriteListPresenter {

    private static MeteoriteListView mView;
    private MeteoriteService meteoriteFetchService;
    private WeakReference<Context> mContextReference = null;
    private GPSTracker mGpsTracker;
    private AddressService mAddressService;
    private MutableLiveData<AddressService.Status> recoveryAddress;

    public MeteoriteListPresenter(Context context) {
        mContextReference = new WeakReference<>(context);
    }

    public LiveData<List<Meteorite>> getMeteorites() {
        final LiveData<List<Meteorite>> data = meteoriteFetchService.getMeteorites();

        final List<Meteorite> meteorites = data.getValue();
        final boolean isEmpty = (meteorites == null || meteorites.isEmpty());

        if (isEmpty) {
            mView.meteoriteLoadingStarted();
            data.observeForever(meteorites1 -> {
                final boolean isNotEmpty = (meteorites1 != null && !meteorites1.isEmpty());
                if (isNotEmpty) {
                    mView.meteoriteLoadingStopped();
                }
            });
        } else {
            mView.hideList();
        }

        if (mContextReference.get() != null && !NetworkUtil.hasConnectivity(mContextReference.get())) {
            mView.unableToFetch();
        }

        return data;
    }


    public void attachView(MeteoriteListView meteoriteListView) {
        mView = meteoriteListView;
        if (mContextReference.get() != null) {
            mGpsTracker = new GPSTracker(meteoriteListView.getGPSDelegate());
            meteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(mContextReference.get(), mGpsTracker);
            if (mAddressService == null) {
                mAddressService = new AddressService();
                recoveryAddress = mAddressService.recoveryAddress();
            }
        }
    }

    public void updateLocation() {
        if (mGpsTracker != null) {
            mGpsTracker.startLocationService();
        }
    }

    public LiveData<Meteorite> getMeteorite(Meteorite meteorite) {
        final Context context = mContextReference.get();
        LiveData<Meteorite> meteoriteLiveData = null;
        if (context != null) {
            final MeteoriteDao meteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(context);
            meteoriteLiveData = meteoriteDao.getMeteoriteById(String.valueOf(meteorite.getId()));
        }
        return meteoriteLiveData;
    }

    public MutableLiveData<AddressService.Status> getRecoveryAddress() {
        return recoveryAddress;
    }
}
