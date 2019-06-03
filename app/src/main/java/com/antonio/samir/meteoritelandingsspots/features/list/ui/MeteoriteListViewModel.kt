package com.antonio.samir.meteoritelandingsspots.features.list.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.antonio.samir.meteoritelandingsspots.features.list.presenter.MeteoriteListPresenter
import com.antonio.samir.meteoritelandingsspots.model.Meteorite


class MeteoriteListViewModel(application: Application) : AndroidViewModel(application) {

    private var presenter: MeteoriteListPresenter? = null
    private var meteorites: LiveData<List<Meteorite>>? = null

    fun getPresenter(): MeteoriteListPresenter {
        if (presenter == null) {
            val context = getApplication<Application>().applicationContext
            presenter = MeteoriteListPresenter(context)
        }
        return presenter as MeteoriteListPresenter
    }

    fun getMeteorites(): LiveData<List<Meteorite>>? {
        if (meteorites == null) {
            meteorites = presenter!!.meteorites
        }
        return meteorites
    }
}
