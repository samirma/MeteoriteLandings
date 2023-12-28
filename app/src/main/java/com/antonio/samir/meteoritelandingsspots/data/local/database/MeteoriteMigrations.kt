package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object MeteoriteMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE meteorites " + " ADD COLUMN address VARCHAR")

            database.execSQL("UPDATE meteorites SET address = (SELECT address FROM address WHERE address._id = _id)")

        }
    }

    val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {

        }
    }

}
