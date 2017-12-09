package com.antonio.samir.meteoritelandingsspots.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class GeoLocationUtil {

    public static Address getAddress(Double latitude, Double longitude, final Context context) {
        Geocoder geocoder;

        geocoder = new Geocoder(context, Locale.getDefault());

        Address address = null;

        try {

            final List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            if (addresses != null && !addresses.isEmpty()) {

                address = addresses.get(0);

            }
        } catch (IOException e) {
            Log.e(GeoLocationUtil.class.getSimpleName(), e.getMessage(), e);
        }

        return address;
    }

}
