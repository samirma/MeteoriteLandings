package com.antonio.samir.meteoritelandingsspots.data.local.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "meteorites", indices = [Index("id")])
data class Meteorite(
    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0,
    var mass: String? = null,
    var nametype: String? = null,
    var recclass: String? = null,
    var name: String? = null,
    var fall: String? = null,
    var year: String? = null,
    var reclong: String? = null,
    var reclat: String? = null,
    var address: String? = null
)
