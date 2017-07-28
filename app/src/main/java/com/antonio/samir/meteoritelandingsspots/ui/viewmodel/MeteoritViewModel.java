package com.antonio.samir.meteoritelandingsspots.ui.viewmodel;

import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

/**
 * Created by samir.antonio on 28/07/2017.
 */
public class MeteoritViewModel extends AndroidViewModel {

    private final MeteoriteService mMeteoriteFetchService;

    public MeteoritViewModel(final Application application) {
        super(application);
        mMeteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(application);
    }

}
