package com.antonio.samir.meteoritelandingsspots.service.server.nasa

import android.os.AsyncTask
import android.util.Log
import com.antonio.samir.meteoritelandingsspots.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerResult
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao

class MeteoriteNasaAsyncTaskService(private val mNasaService: NasaService, private val mMeteoriteDao: MeteoriteDao) : AsyncTask<Void, Void, MeteoriteServerResult>() {

    override fun doInBackground(vararg params: Void): MeteoriteServerResult {

        val meteoriteServerResult = MeteoriteServerResult()

        val meteorites: List<Meteorite>?
        try {
            meteorites = mNasaService.meteorites
            meteoriteServerResult.meteorites = meteorites
        } catch (e: MeteoriteServerException) {
            meteoriteServerResult.setException(e)
        }

        saveMeteorites(meteoriteServerResult)

        return meteoriteServerResult
    }

    private fun saveMeteorites(result: MeteoriteServerResult) {
        try {
            val meteorites = result.meteorites

            meteorites?.let { mMeteoriteDao.insertAll(it) }

            val addressService = AddressService()

            addressService.recoveryAddress()

        } catch (e: MeteoriteServerException) {
            Log.e(TAG, e.message, e)
        }

    }

    companion object {

        val TAG = MeteoriteNasaAsyncTaskService::class.java.simpleName
    }

}
