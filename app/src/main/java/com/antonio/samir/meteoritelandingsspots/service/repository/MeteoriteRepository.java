package com.antonio.samir.meteoritelandingsspots.service.repository;

import com.antonio.samir.meteoritelandingsspots.service.repository.database.AppDatabase;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

public class MeteoriteRepository {

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE meteorites "
                    + " ADD COLUMN address VARCHAR");

            database.execSQL("UPDATE meteorites SET address = (SELECT address FROM address WHERE address._id = _id)");

        }
    };
    private final AppDatabase appDatabase;

    public MeteoriteRepository(Context context) {
        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "meteorites").addMigrations(MIGRATION_1_2).build();

    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
