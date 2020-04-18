package com.antonio.samir.meteoritelandingsspots.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite

@Database(entities = arrayOf(Meteorite::class), version = 3)
abstract class AppDataBase : RoomDatabase() {

    abstract fun meteoriteDao(): MeteoriteDao

}
