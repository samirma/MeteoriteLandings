package com.antonio.samir.meteoritelandingsspots.service.repository

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.migration.Migration
import android.content.Context

import com.antonio.samir.meteoritelandingsspots.service.repository.database.AppDatabase
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao

object MeteoriteRepositoryFactory {

    private var appDatabase: AppDatabase? = null

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE meteorites " + " ADD COLUMN address VARCHAR")

            database.execSQL("UPDATE meteorites SET address = (SELECT address FROM address WHERE address._id = _id)")

        }
    }

    fun getMeteoriteDao(context: Context): MeteoriteDao {
        if (appDatabase == null) {
            appDatabase = getAppDatabase(context)
        }
        return appDatabase!!.meteoriteDao()
    }

    private fun getAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context,
                AppDatabase::class.java, "meteorites").addMigrations(MIGRATION_1_2).build()
    }
}
