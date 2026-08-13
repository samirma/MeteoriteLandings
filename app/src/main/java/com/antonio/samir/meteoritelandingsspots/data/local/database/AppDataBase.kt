package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antonio.samir.meteoritelandingsspots.data.local.model.MeteoriteEntity

const val DATABASE_NAME = "meteorites_v2.db"

/**
 * The database is created from the `meteorites_v2.db` asset, which is at schema v1. Room copies
 * it and then applies [MIGRATION_1_2], so both fresh installs and upgrades converge on v2.
 */
@Database(entities = [MeteoriteEntity::class], version = 2, exportSchema = true)
abstract class AppDataBase : RoomDatabase() {

    abstract fun meteoriteDao(): MeteoriteDao
}
