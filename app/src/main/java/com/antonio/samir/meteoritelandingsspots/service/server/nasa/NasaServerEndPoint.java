package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface needed by retrofit library in order to request from the server
 */
public interface NasaServerEndPoint {

    String URL = "https://data.nasa.gov";


    @GET("resource/y77d-th95.json")
    Call<Meteorite> getPublicMeteorites();

}
