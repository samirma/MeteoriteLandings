package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Interface needed by retrofit library in order to request from the server
 */
public interface NasaServerEndPoint {

    String URL = "https://data.nasa.gov";


    @GET("resource/y77d-th95.json")
    Call<List<Meteorite>> getPublicMeteorites();

}
