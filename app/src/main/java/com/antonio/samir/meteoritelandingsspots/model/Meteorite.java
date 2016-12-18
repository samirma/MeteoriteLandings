package com.antonio.samir.meteoritelandingsspots.model;

public class Meteorite
{
    private String mass;

    private String id;

    private String nametype;

    private String recclass;

    private Geolocation geolocation;

    private String name;

    private String fall;

    private String year;

    private String reclong;

    private String reclat;

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
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
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

    public Geolocation getGeolocation ()
    {
        return geolocation;
    }

    public void setGeolocation (Geolocation geolocation)
    {
        this.geolocation = geolocation;
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


}
