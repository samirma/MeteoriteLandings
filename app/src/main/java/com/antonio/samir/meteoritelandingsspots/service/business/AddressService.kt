package com.antonio.samir.meteoritelandingsspots.service.business

import android.content.Context
import android.util.Log
import androidx.annotation.StringDef
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.service.business.AddressService.Status.Companion.DONE
import com.antonio.samir.meteoritelandingsspots.service.business.AddressService.Status.Companion.LOADING
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.repository.local.MeteoriteRepositoryInterface
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.lang3.StringUtils
import java.util.*


class AddressService(
        val context: Context,
        val meteoriteRepository: MeteoriteRepositoryInterface,
        val geoLocationUtil: GeoLocationUtilInterface
) : AddressServiceInterface {

    val TAG = AddressService::class.java.simpleName

    override val status = MutableLiveData<String>()

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(DONE, LOADING)
    annotation class Status {
        companion object {
            const val DONE = "DONE"
            const val LOADING = "LOADING"
        }
    }

    override fun recoveryAddress() {

        GlobalScope.launch(Dispatchers.Default) {
            if (status.value == null || status.value === DONE) {
                val meteorites = meteoriteRepository.meteoritesWithOutAddress()
                try {
                    if (meteorites.isNotEmpty()) {
                        Log.i(TAG, "recoveryAddress $LOADING")
                        status.postValue(LOADING)

                        meteorites.onEach { meteorite ->
                            recoverAddress(meteorite)
                        }

                        Log.i(TAG, "recoveryAddress $DONE")
                        status.postValue(DONE)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Fail to retrieve address", e)
                }

            }

        }

    }

    private suspend fun recoverAddress(meteorite: Meteorite) = withContext(Dispatchers.Default) {
        val recLat = meteorite.reclat
        val recLong = meteorite.reclong

        val address = getAddress(recLat, recLong)
        meteorite.address = address
        meteoriteRepository.update(meteorite)
//        Log.i(TAG, String.format("Address for id %s recovered", meteorite.id))

    }


    private fun getAddress(recLat: String?, recLong: String?): String {
        var addressString = ""
        if (StringUtils.isNoneEmpty(recLat) && StringUtils.isNoneEmpty(recLong)) {
            val address = geoLocationUtil.getAddress(java.lang.Double.parseDouble(recLat), java.lang.Double.parseDouble(recLong))
            if (address != null) {
                val finalAddress = ArrayList<String>()
                val city = address.locality
                if (StringUtils.isNoneEmpty(city)) {
                    finalAddress.add(city)
                }

                val state = address.adminArea
                if (StringUtils.isNoneEmpty(state)) {
                    finalAddress.add(state)
                }

                val countryName = address.countryName
                if (StringUtils.isNoneEmpty(countryName)) {
                    finalAddress.add(countryName)
                }

                addressString = StringUtils.join(finalAddress, ", ")

            }
        }

        return addressString
    }
}
