package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.content.ContentProviderOperation;

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
        builder.withValue(MeteoriteColumns.MESS, meteorite.getId());
        builder.withValue(MeteoriteColumns.NAMETYPE, meteorite.getId());
        builder.withValue(MeteoriteColumns.RECCLASS, meteorite.getId());
        builder.withValue(MeteoriteColumns.NAME, meteorite.getId());
        builder.withValue(MeteoriteColumns.FALL, meteorite.getId());
        builder.withValue(MeteoriteColumns.YEAR, meteorite.getId());
        builder.withValue(MeteoriteColumns.RECLONG, meteorite.getId());
        builder.withValue(MeteoriteColumns.RECLAT, meteorite.getId());

        return builder.build();
    }
}

