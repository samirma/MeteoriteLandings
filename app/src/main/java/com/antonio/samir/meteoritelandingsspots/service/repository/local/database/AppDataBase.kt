package com.antonio.samir.meteoritelandingsspots.service.repository.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

@Database(entities = arrayOf(Meteorite::class), version = 3)
abstract class AppDataBase : RoomDatabase() {

    abstract fun meteoriteDao(): MeteoriteDao

}
