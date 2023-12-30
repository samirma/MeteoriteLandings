package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite

const val DATABASE_NAME = "meteorites_v2.db"


@Database(entities = [Meteorite::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun meteoriteDao(): MeteoriteDao

}
