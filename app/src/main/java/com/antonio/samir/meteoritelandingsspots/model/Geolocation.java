package com.antonio.samir.meteoritelandingsspots.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Geolocation implements Parcelable {
    private String type;

    private String[] coordinates;

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public String[] getCoordinates ()
    {
        return coordinates;
    }

    public void setCoordinates (String[] coordinates)
    {
        this.coordinates = coordinates;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeStringArray(this.coordinates);
    }

    public Geolocation() {
    }

    protected Geolocation(Parcel in) {
        this.type = in.readString();
        this.coordinates = in.createStringArray();
    }

    public static final Parcelable.Creator<Geolocation> CREATOR = new Parcelable.Creator<Geolocation>() {
        @Override
        public Geolocation createFromParcel(Parcel source) {
            return new Geolocation(source);
        }

        @Override
        public Geolocation[] newArray(int size) {
            return new Geolocation[size];
        }
    };
}
