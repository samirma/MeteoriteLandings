package com.antonio.samir.meteoritelandingsspots.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter;

import java.util.List;


public class MeteoriteViewModel extends AndroidViewModel {

    private MeteoriteListPresenter presenter;
    private LiveData<List<Meteorite>> meteorites = null;

    public MeteoriteViewModel(final Application application) {
        super(application);
    }

    public MeteoriteListPresenter getPresenter() {
        if (presenter == null) {
            final Context context = getApplication().getApplicationContext();
            presenter = new MeteoriteListPresenter(context);
        }
        return presenter;
    }

    public LiveData<List<Meteorite>> getMeteorites() {
        if (meteorites == null) {
            meteorites = presenter.getMeteorites();
        }
        return meteorites;
    }
}
