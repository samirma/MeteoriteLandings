package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;


@ContentProvider(authority = MeteoriteProvider.AUTHORITY, database = MeteoriteDatabase.class)
public class MeteoriteProvider {
    public static final String AUTHORITY = "com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider";

    private static final String METEORITES = "/meteorites";
    private static final String ADDRESSES = "/addresses";


    interface Path {
        String METEORITES = "meteorites";
        String ADDRESSES = "addresses";
    }

    @TableEndpoint(table = MeteoriteDatabase.METEORITES)
    public static class Meteorites {
        @ContentUri(
                path = Path.METEORITES,
                type = "vnd.android.cursor.dir/meteorite",
                defaultSort = MeteoriteColumns.ID + " ASC")
        public static final Uri LISTS = Uri.parse("content://" + AUTHORITY + METEORITES);

        @InexactContentUri(
                path = Path.METEORITES + "/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/meteorite",
                whereColumn = MeteoriteColumns.ID,
                pathSegment = 1)
        public static Uri withId(String id) {
            return Uri.parse(String.format("content://%s%s/%s", AUTHORITY, METEORITES, id));
        }

    }

    @TableEndpoint(table = MeteoriteDatabase.ADDRESSES)
    public static class Addresses {
        @ContentUri(
                path = Path.ADDRESSES,
                type = "vnd.android.cursor.dir/address",
                defaultSort = AddressColumns.ID + " ASC")
        public static final Uri LISTS = Uri.parse("content://" + AUTHORITY + ADDRESSES);

        @InexactContentUri(
                path = Path.ADDRESSES + "/#",
                name = "LIST_ID",
                type = "vnd.android.cursor.item/address",
                whereColumn = AddressColumns.ID,
                pathSegment = 1)
        public static Uri withId(String id) {
            return Uri.parse(String.format("content://%s%s/%s", AUTHORITY, ADDRESSES, id));
        }

    }

}