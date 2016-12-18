package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
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
                path = Path.METEORITES,
                type = "vnd.android.cursor.dir/meteorite"
        )
        public static final Uri CONTENT_URI = buildUri(Path.METEORITES);

        @InexactContentUri(
                name = "QUOTE_ID",
                path = Path.METEORITES + "/*",
                type = "vnd.android.cursor.item/meteorites",
                whereColumn = MeteoriteColumns.ID,
                pathSegment = 1
        )
        public static Uri meteorites() {
            return buildUri(Path.METEORITES);
        }
    }
}
