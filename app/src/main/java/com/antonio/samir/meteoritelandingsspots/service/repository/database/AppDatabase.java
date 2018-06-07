package com.antonio.samir.meteoritelandingsspots.service.repository.database;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Meteorite.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract MeteoriteDao meteoriteDao();

}
