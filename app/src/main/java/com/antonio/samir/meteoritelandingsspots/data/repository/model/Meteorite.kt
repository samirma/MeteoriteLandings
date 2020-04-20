package com.antonio.samir.meteoritelandingsspots.data.repository.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "meteorites", indices = [Index("id")])
@Parcelize
class Meteorite : Parcelable {
    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0
    var mass: String? = null
    var nametype: String? = null
    var recclass: String? = null
    var name: String? = null
    var fall: String? = null
    var year: String? = null
    var reclong: String? = null
    var reclat: String? = null
    var address: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Meteorite

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id
    }

}
