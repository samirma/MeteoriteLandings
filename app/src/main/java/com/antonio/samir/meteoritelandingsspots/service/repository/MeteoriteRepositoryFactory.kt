package com.antonio.samir.meteoritelandingsspots.service.repository

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.antonio.samir.meteoritelandingsspots.service.repository.database.AppDataBase
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao

object MeteoriteRepositoryFactory {

    private var appDatabase: AppDataBase? = null

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

    private fun getAppDatabase(context: Context): AppDataBase {
        return Room.databaseBuilder(context,
                AppDataBase::class.java, "meteorites").addMigrations(MIGRATION_1_2).build()
    }
}
