package com.antonio.samir.meteoritelandingsspots.service.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antonio.samir.meteoritelandingsspots.model.Meteorite

@Database(entities = arrayOf(Meteorite::class), version = 2)
abstract class AppDataBase : RoomDatabase() {

    abstract fun meteoriteDao(): MeteoriteDao

}
