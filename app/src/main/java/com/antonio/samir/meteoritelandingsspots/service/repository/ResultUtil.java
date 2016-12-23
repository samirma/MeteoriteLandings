package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.server.AddressService;

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

    public static ArrayList quoteJsonToContentVals(List<Meteorite> list, ContentResolver contentResolver) {
        ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

        final AddressService addressService = new AddressService(contentResolver);

        for (Meteorite meteorite:list) {

            final ContentProviderOperation e = buildBatchOperation(meteorite, addressService);
            batchOperations.add(e);

        }

        return batchOperations;
    }


    public static ContentProviderOperation buildBatchOperation(final Meteorite meteorite, AddressService addressService) {
        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
                MeteoriteProvider.Meteorites.LISTS);

        final String id = meteorite.getId();
        builder.withValue(MeteoriteColumns.ID, id);

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

        final String recLat = getValue(meteorite.getReclat());
        final String recLong = getValue(meteorite.getReclong());

        builder.withValue(MeteoriteColumns.RECLONG, recLong);
        builder.withValue(MeteoriteColumns.RECLAT, recLat);

        addressService.recoverAddress(id, recLat, recLong);

        return builder.build();
    }

    @NonNull
    private static String getValue(String value) {
        return (value == null) ? "" : value;
    }
}

