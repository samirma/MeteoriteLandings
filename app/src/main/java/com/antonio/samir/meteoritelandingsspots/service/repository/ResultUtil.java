package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.content.ContentProviderOperation;
import android.location.Address;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.Application;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.server.GeoLocationUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * ResultUtil in order to help to create the builk insert
 */
public class ResultUtil {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final String TAG = ResultUtil.class.getSimpleName();

    public static ArrayList quoteJsonToContentVals(List<Meteorite> list) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        for (Meteorite meteorite:list) {

            final ContentProviderOperation e = buildBatchOperation(meteorite);
            batchOperations.add(e);

        }

        return batchOperations;
    }
    


    public static ContentProviderOperation buildBatchOperation(final Meteorite meteorite) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                MeteoriteProvider.Meteorites.LISTS);

        builder.withValue(MeteoriteColumns.ID, meteorite.getId());

        builder.withValue(MeteoriteColumns.MASS, getValue(meteorite.getMass()));

        builder.withValue(MeteoriteColumns.NAMETYPE, meteorite.getNametype());
        builder.withValue(MeteoriteColumns.RECCLASS, meteorite.getRecclass());
        builder.withValue(MeteoriteColumns.NAME, meteorite.getName());
        builder.withValue(MeteoriteColumns.FALL, meteorite.getFall());

        final String year = meteorite.getYear();
        String value = getValue(year);
        String yearParsed = value;
        if (!TextUtils.isEmpty(value)) {
            try {
                final Date date = SIMPLE_DATE_FORMAT.parse(year.trim());

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                yearParsed = String.valueOf(cal.get(Calendar.YEAR));

            } catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        builder.withValue(MeteoriteColumns.YEAR, yearParsed);

        final String recLong = getValue(meteorite.getReclong());
        final String recLat = getValue(meteorite.getReclat());

        builder.withValue(MeteoriteColumns.RECLONG, recLong);
        builder.withValue(MeteoriteColumns.RECLAT, recLat);

        String addressString = "";
        if (!TextUtils.isEmpty(recLat) && !TextUtils.isEmpty(recLong)) {
            final Address address = GeoLocationUtil.getAddress(Double.parseDouble(recLat), Double.parseDouble(recLong), Application.getContext());
            if (address != null) {

                final String city = address.getLocality();
                if (!TextUtils.isEmpty(city)) {
                    addressString = addressString + city;
                }

                final String state = address.getAdminArea();
                if (!TextUtils.isEmpty(state)) {
                    addressString = addressString + ", " + state;
                }

                final String countryName = address.getCountryName();
                if (!TextUtils.isEmpty(countryName)) {
                    if (!TextUtils.isEmpty(countryName)) {
                        addressString = addressString + ", " + countryName;
                    }
                }
            }
        }

        builder.withValue(MeteoriteColumns.ADDRESS, addressString);



        return builder.build();
    }

    @NonNull
    private static String getValue(String value) {
        return (value == null) ? "" : value;
    }
}

