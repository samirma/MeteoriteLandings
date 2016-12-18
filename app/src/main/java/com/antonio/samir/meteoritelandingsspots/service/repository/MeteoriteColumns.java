package com.antonio.samir.meteoritelandingsspots.service.repository;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public class MeteoriteColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    public static final String ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String mass = "mass";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String nametype = "nametype";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String recclass = "recclass";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String name = "name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String fall = "fall";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String year = "year";

    @DataType(DataType.Type.REAL)
    @NotNull
    public static final String reclong = "reclong";

    @DataType(DataType.Type.REAL)
    @NotNull
    public static final String reclat = "REAL";
}
