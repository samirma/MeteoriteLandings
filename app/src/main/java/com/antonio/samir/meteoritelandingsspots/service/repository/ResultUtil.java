package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.content.ContentProviderOperation;
import android.support.annotation.NonNull;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import java.util.ArrayList;
import java.util.List;

/**
 * ResultUtil in order to help to create the builk insert
 */
public class ResultUtil {

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

        builder.withValue(MeteoriteColumns.YEAR, getValue(meteorite.getYear()));

        builder.withValue(MeteoriteColumns.RECLONG, getValue(meteorite.getReclong()));
        builder.withValue(MeteoriteColumns.RECLAT, getValue(meteorite.getReclat()));

        return builder.build();
    }

    @NonNull
    private static String getValue(String value) {
        return (value == null) ? "" : value;
    }
}

