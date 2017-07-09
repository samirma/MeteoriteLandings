package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.service.repository.database.AppDatabase;

public class MeteoriteRepository {

    private final AppDatabase appDatabase;

    public MeteoriteRepository(Context context) {
        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "meteorites").build();

    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
