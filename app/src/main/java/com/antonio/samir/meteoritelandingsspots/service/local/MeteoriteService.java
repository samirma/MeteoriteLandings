package com.antonio.samir.meteoritelandingsspots.service.local;

import android.arch.lifecycle.LiveData;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import java.util.List;

public interface MeteoriteService {

    LiveData<List<Meteorite>> getMeteorites();

}
