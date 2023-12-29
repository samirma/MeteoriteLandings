package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite

@Database(entities = [Meteorite::class], version = 3)
abstract class AppDataBase : RoomDatabase() {

    abstract fun meteoriteDao(): MeteoriteDao

}
