package com.antonio.samir.meteoritelandingsspots.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "meteorites", indices = {@Index("_id")})
public class Meteorite implements Parcelable {

    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static final String TAG = Meteorite.class.getSimpleName();

    @PrimaryKey
    private String _id;
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

    public String getMass ()
    {
        return mass;
    }

    public void setMass (String mass)
    {
        this.mass = mass;
    }

    public String getId ()
    {
        return _id;
    }

    public void setId (String id)
    {
        this._id = id;
    }

    public String getNametype ()
    {
        return nametype;
    }

    public void setNametype (String nametype)
    {
        this.nametype = nametype;
    }

    public String getRecclass ()
    {
        return recclass;
    }

    public void setRecclass (String recclass)
    {
        this.recclass = recclass;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getFall ()
    {
        return fall;
    }

    public void setFall (String fall)
    {
        this.fall = fall;
    }

    public String getYear ()
    {
        return year;
    }

    public void setYear (String year)
    {
        this.year = year;
    }

    public String getReclong ()
    {
        return reclong;
    }

    public void setReclong (String reclong)
    {
        this.reclong = reclong;
    }

    public String getReclat ()
    {
        return reclat;
    }

    public void setReclat (String reclat)
    {
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
        dest.writeString(this._id);
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

    protected Meteorite(Parcel in) {
        this._id = in.readString();
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
}
