package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


@ContentProvider(authority = MeteoriteProvider.AUTHORITY, database = MeteoriteDatabase.class)
public class MeteoriteProvider {
    public static final String AUTHORITY = "com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider";

    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String METEORITES = "meteorites";
    }

    @TableEndpoint(table = MeteoriteDatabase.METEORITES)
    public static class Meteorites {
        @ContentUri(
                path = "lists",
                type = "vnd.android.cursor.dir/meteorite",
                defaultSort = MeteoriteColumns.ID + " ASC")
        public static final Uri LISTS = Uri.parse("content://" + AUTHORITY + "/lists");

        @InexactContentUri(
                path = Path.METEORITES + "/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/meteorite",
                whereColumn = MeteoriteColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return Uri.parse("content://" + AUTHORITY + "/lists/" + id);
        }

    }
}