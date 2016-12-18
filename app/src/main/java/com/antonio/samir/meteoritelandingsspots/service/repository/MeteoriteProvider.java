package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


@ContentProvider(authority = MeteoriteProvider.AUTHORITY, database = MeteoriteDatabase.class)
public class MeteoriteProvider {
    public static final String AUTHORITY = "com.sam_chordas.android.stockhawk.data.MeteoriteProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    interface Path {
        String METEORITES = "meteorites";
    }

    @TableEndpoint(table = MeteoriteDatabase.Meteorites)
    public static class Meteorites {
        @ContentUri(
                path = "lists",
                type = "vnd.android.cursor.dir/list",
                defaultSort = MeteoriteColumns.ID + " ASC")
        public static final Uri LISTS = Uri.parse("content://" + AUTHORITY + "/lists");

    }
}
