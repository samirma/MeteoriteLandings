package com.antonio.samir.meteoritelandingsspots.service.business.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "meteorites", indices = arrayOf(Index("id")))
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
        if (mass != other.mass) return false
        if (nametype != other.nametype) return false
        if (recclass != other.recclass) return false
        if (name != other.name) return false
        if (fall != other.fall) return false
        if (year != other.year) return false
        if (reclong != other.reclong) return false
        if (reclat != other.reclat) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (mass?.hashCode() ?: 0)
        result = 31 * result + (nametype?.hashCode() ?: 0)
        result = 31 * result + (recclass?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (fall?.hashCode() ?: 0)
        result = 31 * result + (year?.hashCode() ?: 0)
        result = 31 * result + (reclong?.hashCode() ?: 0)
        result = 31 * result + (reclat?.hashCode() ?: 0)
        return result
    }

}
