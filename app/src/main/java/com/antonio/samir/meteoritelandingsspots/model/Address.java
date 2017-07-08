package com.antonio.samir.meteoritelandingsspots.model;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by samirantonio on 08/07/17.
 */
@Entity(tableName = "addresses")
public class Address {
    @PrimaryKey
    private Integer _id;
    private String address;
}
