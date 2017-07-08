package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.antonio.samir.meteoritelandingsspots.model.Address;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

@Database(entities = {Meteorite.class, Address.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AddressDao addressDao();

    public abstract MeteoriteDao meteoriteDao();

}
