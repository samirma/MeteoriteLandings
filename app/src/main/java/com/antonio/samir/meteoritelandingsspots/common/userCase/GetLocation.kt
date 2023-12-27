package com.antonio.samir.meteoritelandingsspots.common.userCase

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.GetLocation.Input
import com.antonio.samir.meteoritelandingsspots.common.userCase.GetLocation.Output
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetLocation @Inject constructor(
    private val requestPermission: RequestPermission,
    private val locationManager: LocationManager
) : UserCaseBase<Input, ResultOf<Output>>() {

    override fun action(input: Input) = requestPermission(
        RequestPermission.Input(
            activity = input.activity,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
        .map {
            when (it) {
                is ResultOf.Success -> ResultOf.Success(Output(getLocation()))
                is ResultOf.InProgress -> ResultOf.Success(Output(null))
                is ResultOf.Error -> it
            }
        }

    private fun isGPSEnabled(): Boolean {

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    }

    @SuppressLint("MissingPermission")
    private fun getLocation() = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

    class Input(val activity: AppCompatActivity)

    data class Output(val location: Location?)

}