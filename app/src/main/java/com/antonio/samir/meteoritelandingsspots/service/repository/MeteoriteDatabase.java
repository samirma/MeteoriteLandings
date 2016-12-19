package com.antonio.samir.meteoritelandingsspots.service.repository;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;


@Database(version = MeteoriteDatabase.VERSION)
public class MeteoriteDatabase {
    public static final int VERSION = 1;
    @Table(MeteoriteColumns.class)
    public static final String METEORITES = "meteorites";

    private MeteoriteDatabase() {
    }
}
