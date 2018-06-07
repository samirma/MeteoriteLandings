package com.antonio.samir.meteoritelandingsspots.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "meteorites", indices = {@Index("id")})
public class Meteorite implements Parcelable {

    public static final String TAG = Meteorite.class.getSimpleName();
    public static final Creator<Meteorite> CREATOR = new Creator<Meteorite>() {
        @Override
        public Meteorite createFromParcel(Parcel source) {
            return new Meteorite(source);
        }

        @Override
        public Meteorite[] newArray(int size) {
            return new Meteorite[size];
        }
    };
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    @PrimaryKey
    @SerializedName("id")
    protected int id;
    private String mass;
    private String nametype;
    private String recclass;
    private String name;
    private String fall;
    private String year;
    private String reclong;
    private String reclat;
    private String address;

    public Meteorite() {
    }

    protected Meteorite(Parcel in) {
        this.id = in.readInt();
        this.mass = in.readString();
        this.nametype = in.readString();
        this.recclass = in.readString();
        this.name = in.readString();
        this.fall = in.readString();
        this.year = in.readString();
        this.reclong = in.readString();
        this.reclat = in.readString();
        this.address = in.readString();
    }

    public String getYearString() {
        String value = year;
        String yearParsed = value;
        if (!TextUtils.isEmpty(value)) {
            try {
                final Date date = SIMPLE_DATE_FORMAT.parse(year.trim());

                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                yearParsed = String.valueOf(cal.get(Calendar.YEAR));

            } catch (ParseException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return yearParsed;
    }

    public String getMass() {
        return mass;
    }

    public void setMass(String mass) {
        this.mass = mass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNametype() {
        return nametype;
    }

    public void setNametype(String nametype) {
        this.nametype = nametype;
    }

    public String getRecclass() {
        return recclass;
    }

    public void setRecclass(String recclass) {
        this.recclass = recclass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFall() {
        return fall;
    }

    public void setFall(String fall) {
        this.fall = fall;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getReclong() {
        return reclong;
    }

    public void setReclong(String reclong) {
        this.reclong = reclong;
    }

    public String getReclat() {
        return reclat;
    }

    public void setReclat(String reclat) {
        this.reclat = reclat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.mass);
        dest.writeString(this.nametype);
        dest.writeString(this.recclass);
        dest.writeString(this.name);
        dest.writeString(this.fall);
        dest.writeString(this.year);
        dest.writeString(this.reclong);
        dest.writeString(this.reclat);
        dest.writeString(this.address);
    }

    public double distance(double latitude, double longitude) {
        double result = -1;
        if (StringUtils.isNotEmpty(reclat) && StringUtils.isNotEmpty(reclong)) {
            try {
                result = Math.abs(Float.valueOf(reclat) - latitude) + Math.abs(Float.valueOf(reclong) - longitude);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return result;
    }
}
