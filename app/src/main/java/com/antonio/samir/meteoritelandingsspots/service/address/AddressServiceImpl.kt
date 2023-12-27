package com.antonio.samir.meteoritelandingsspots.service.address

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.data.local.MeteoriteLocalRepository
import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.util.GeoLocationUtilInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

@ExperimentalCoroutinesApi
class AddressServiceImpl(
    private val meteoriteLocalRepository: MeteoriteLocalRepository,
    private val geoLocationUtil: GeoLocationUtilInterface
) : AddressServiceInterface {

    companion object {
        private val TAG = AddressServiceImpl::class.java.simpleName
    }

    override fun recoveryAddress(): Flow<ResultOf<Float>> =
        meteoriteLocalRepository.meteoritesWithOutAddress()
            .onEach { recoverAddress(it) }
            .map {
                getReturn(it)
            }
            .catch { throwable ->
                emit(ResultOf.Error(Exception("Fail to load addresses", throwable)))
            }.flowOn(Dispatchers.Default)

    private suspend fun getReturn(it: List<Meteorite>): ResultOf<Float> {
        return if (it.isNotEmpty()) {
            val meteoritesWithoutAddressCount =
                meteoriteLocalRepository.getMeteoritesWithoutAddressCount()
            val meteoritesCount = meteoriteLocalRepository.getValidMeteoritesCount()
            val progress = (1 - (meteoritesWithoutAddressCount.toFloat() / meteoritesCount)) * 100
            Log.d(
                TAG,
                "meteoritesWithoutAddressCount: $meteoritesWithoutAddressCount meteoritesCount: $meteoritesCount progress: $progress"
            )
            ResultOf.InProgress(progress)
        } else {
            ResultOf.Success(100f)
        }
    }

    private suspend fun recoverAddress(list: List<Meteorite>) {
        list.onEach { meteorite ->
            meteorite.address = getAddressFromMeteorite(meteorite)
        }
        meteoriteLocalRepository.updateAll(list)
    }

    override suspend fun recoverAddress(meteorite: Meteorite) = withContext(Dispatchers.Default) {
        meteorite.address = getAddressFromMeteorite(meteorite)
        meteoriteLocalRepository.update(meteorite)
    }

    private fun getAddressFromMeteorite(meteorite: Meteorite): String {
        val recLat = meteorite.reclat
        val recLong = meteorite.reclong

        var metAddress = " "
        if (recLat != null && recLong != null) {
            val address = getAddress(recLat.toDouble(), recLong.toDouble())
            metAddress = address ?: " "
        }
        return metAddress
    }


    private fun getAddress(recLat: Double, recLong: Double): String? {
        var addressString: String? = null
        val address = geoLocationUtil.getAddress(recLat, recLong)
        if (address != null) {
            val finalAddress = ArrayList<String>()
            val city = address.locality
            if (!city.isNullOrBlank()) {
                finalAddress.add(city)
            }

            val state = address.adminArea
            if (!state.isNullOrBlank()) {
                finalAddress.add(state)
            }

            val countryName = address.countryName
            if (!countryName.isNullOrBlank()) {
                finalAddress.add(countryName)
            }

            if (finalAddress.isNotEmpty()) {
                addressString = finalAddress.joinToString(", ")
            }

        }

        return addressString
    }
}
