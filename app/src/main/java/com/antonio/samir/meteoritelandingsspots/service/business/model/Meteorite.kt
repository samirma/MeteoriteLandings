package com.antonio.samir.meteoritelandingsspots.service.business.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "meteorites", indices = arrayOf(Index("id")))
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

    val yearString: String?
        get() {
            val value = year
            var yearParsed = value
            if (!TextUtils.isEmpty(value)) {
                try {
                    val date = SIMPLE_DATE_FORMAT.parse(year!!.trim { it <= ' ' })

                    val cal = Calendar.getInstance()
                    cal.time = date
                    yearParsed = cal.get(Calendar.YEAR).toString()

                } catch (e: ParseException) {
                    Log.e(TAG, e.message, e)
                }

            }
            return yearParsed
        }

    constructor()

    protected constructor(`in`: Parcel) {
        this.id = `in`.readInt()
        this.mass = `in`.readString()
        this.nametype = `in`.readString()
        this.recclass = `in`.readString()
        this.name = `in`.readString()
        this.fall = `in`.readString()
        this.year = `in`.readString()
        this.reclong = `in`.readString()
        this.reclat = `in`.readString()
        this.address = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this.id)
        dest.writeString(this.mass)
        dest.writeString(this.nametype)
        dest.writeString(this.recclass)
        dest.writeString(this.name)
        dest.writeString(this.fall)
        dest.writeString(this.year)
        dest.writeString(this.reclong)
        dest.writeString(this.reclat)
        dest.writeString(this.address)
    }

    fun distance(latitude: Double, longitude: Double): Double {
        var result = -1.0
        if (StringUtils.isNotEmpty(reclat) && StringUtils.isNotEmpty(reclong)) {
            try {
                result = Math.abs(java.lang.Float.valueOf(reclat) - latitude) + Math.abs(java.lang.Float.valueOf(reclong) - longitude)
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
            }

        }
        return result
    }

    companion object CREATOR : Parcelable.Creator<Meteorite> {
        override fun createFromParcel(parcel: Parcel): Meteorite {
            return Meteorite(parcel)
        }

        override fun newArray(size: Int): Array<Meteorite?> {
            return arrayOfNulls(size)
        }

        val TAG = Meteorite::class.java.simpleName

        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    }
}
