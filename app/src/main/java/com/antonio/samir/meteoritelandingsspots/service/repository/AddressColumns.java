package com.antonio.samir.meteoritelandingsspots.service.repository;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public class AddressColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    public static final String ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String ADDRESS = "address";
}
