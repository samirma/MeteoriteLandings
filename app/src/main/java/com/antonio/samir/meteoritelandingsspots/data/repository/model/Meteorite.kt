package com.antonio.samir.meteoritelandingsspots.data.repository.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "meteorites", indices = [Index("id")])
@Parcelize
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

) : Parcelable
