package com.antonio.samir.meteoritelandingsspots.service.repository;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import com.antonio.samir.meteoritelandingsspots.service.repository.database.AppDatabase;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;

public class MeteoriteRepositoryFactory {

    private static AppDatabase appDatabase;

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE meteorites "
                    + " ADD COLUMN address VARCHAR");

            database.execSQL("UPDATE meteorites SET address = (SELECT address FROM address WHERE address._id = _id)");

        }
    };

    public static MeteoriteDao getMeteoriteDao(final Context context) {
        if (appDatabase == null) {
            appDatabase = getAppDatabase(context);
        }
        return appDatabase.meteoriteDao();
    }

    private static AppDatabase getAppDatabase(final Context context) {
        return Room.databaseBuilder(context,
                AppDatabase.class, "meteorites").addMigrations(MIGRATION_1_2).build();
    }
}
