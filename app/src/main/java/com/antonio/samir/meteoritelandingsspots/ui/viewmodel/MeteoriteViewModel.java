package com.antonio.samir.meteoritelandingsspots.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter;


public class MeteoriteViewModel extends AndroidViewModel {

    private MeteoriteListPresenter presenter;

    public MeteoriteViewModel(final Application application) {
        super(application);
    }

    public void setPresenter(MeteoriteListPresenter presenter) {
        this.presenter = presenter;
    }

    public MeteoriteListPresenter getPresenter() {
        if (presenter == null) {
            final Context context = getApplication().getApplicationContext();
            presenter = new MeteoriteListPresenter(context);
        }
        return presenter;
    }
}
