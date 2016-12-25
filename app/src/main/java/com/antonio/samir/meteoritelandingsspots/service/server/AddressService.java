package com.antonio.samir.meteoritelandingsspots.service.server;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Address;
import android.net.Uri;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.Application;
import com.antonio.samir.meteoritelandingsspots.service.repository.AddressColumns;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.antonio.samir.meteoritelandingsspots.service.repository.AddressColumns.ADDRESS;

/**
 * Created by samir on 12/23/16.
 */

public class AddressService {

    public static final String TAG = AddressService.class.getSimpleName();
    final static ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 1000,
            1L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());
    private final ContentResolver mContentResolver;

    public AddressService(ContentResolver contentResolver) {
        this.mContentResolver = contentResolver;
    }


    public void recoverAddress(final String id, final String recLat, final String recLong) {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                final String address = getAddress(recLat, recLong);
                final ContentValues mNewValues = new ContentValues();
                mNewValues.put(AddressColumns.ID, id);
                mNewValues.put(AddressColumns.ADDRESS, address);
                mContentResolver.insert(MeteoriteProvider.Addresses.LISTS, mNewValues);
                Log.i(TAG, String.format("Address for id %s recoverd", id));
            }
        });

    }

    private String getAddress(String recLat, String recLong) {
        String addressString = "";
        if (StringUtils.isNoneEmpty(recLat) && StringUtils.isNoneEmpty(recLong)) {
            final Address address = GeoLocationUtil.getAddress(Double.parseDouble(recLat), Double.parseDouble(recLong), Application.getContext());
            if (address != null) {
                final List<String> finalAddress = new ArrayList<>();
                final String city = address.getLocality();
                if (StringUtils.isNoneEmpty(city)) {
                    finalAddress.add(city);
                }

                final String state = address.getAdminArea();
                if (StringUtils.isNoneEmpty(state)) {
                    finalAddress.add(state);
                }

                final String countryName = address.getCountryName();
                if (StringUtils.isNoneEmpty(countryName)) {
                    finalAddress.add(countryName);
                }

                addressString = StringUtils.join(finalAddress, ", ");

            }
        }

        return addressString;
    }

    public String getAddressFromId(String idString) {
        final Uri uri = MeteoriteProvider.Addresses.withId(idString);
        final Cursor cursor = mContentResolver.query(uri,
                new String[]{ADDRESS},
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        String address = "";
        if (cursor.getCount() == 1) {
            address = cursor.getString(cursor.getColumnIndex(ADDRESS));
        }
        return address;
    }
}
