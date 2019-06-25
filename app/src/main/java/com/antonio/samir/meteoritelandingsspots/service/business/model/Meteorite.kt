package com.antonio.samir.meteoritelandingsspots.service.business.model

import android.os.Parcelable
import android.text.TextUtils
import android.util.Log
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.StringUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "meteorites", indices = arrayOf(Index("id")))
@Parcelize
class Meteorite constructor() : Parcelable {
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

    companion object {
        val TAG = Meteorite::class.java.simpleName

        private val SIMPLE_DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    }

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


}
