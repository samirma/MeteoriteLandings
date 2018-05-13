package com.antonio.samir.meteoritelandingsspots.presenter


import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteService
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServiceFactory
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil
import java.lang.ref.WeakReference

/**
 * Presenter layer responsible for manage the interactions between the activity and the services
 */
class MeteoriteListPresenter(context: Context) {
    private var meteoriteFetchService: MeteoriteService? = null
    private var mContextReference: WeakReference<Context>? = null
    private var mGpsTracker: GPSTracker? = null
    private var mAddressService: AddressService? = null
    var recoveryAddress: MutableLiveData<AddressService.Status>? = null
        private set

    val meteorites: LiveData<List<Meteorite>>
        get() {
            val data = meteoriteFetchService!!.meteorites

            val meteorites = data.value
            val isEmpty = meteorites == null || meteorites.isEmpty()

            if (isEmpty) {
                mView!!.meteoriteLoadingStarted()
                data.observeForever { meteorites1 ->
                    val isNotEmpty = meteorites1 != null && !meteorites1.isEmpty()
                    if (isNotEmpty) {
                        mView!!.meteoriteLoadingStopped()
                    }
                }
            } else {
                mView!!.hideList()
            }

            if (mContextReference!!.get() != null && !NetworkUtil.hasConnectivity(mContextReference!!.get())) {
                mView!!.unableToFetch()
            }

            return data
        }

    init {
        mContextReference = WeakReference(context)
    }


    fun attachView(meteoriteListView: MeteoriteListView) {
        mView = meteoriteListView
        if (mContextReference!!.get() != null) {
            mGpsTracker = GPSTracker(meteoriteListView.gpsDelegate)
            meteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(mContextReference!!.get(), mGpsTracker)
            if (mAddressService == null) {
                mAddressService = AddressService()
                recoveryAddress = mAddressService!!.recoveryAddress()
            }
        }
    }

    fun updateLocation() {
        if (mGpsTracker != null) {
            mGpsTracker!!.startLocationService()
        }
    }

    fun getMeteorite(meteorite: Meteorite): LiveData<Meteorite>? {
        val context = mContextReference!!.get()
        var meteoriteLiveData: LiveData<Meteorite>? = null
        if (context != null) {
            val meteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(context)
            meteoriteLiveData = meteoriteDao.getMeteoriteById(meteorite.id.toString())
        }
        return meteoriteLiveData
    }

    companion object {

        private var mView: MeteoriteListView? = null
    }
}
