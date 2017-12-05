package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.local.MeteoriteServerException;

import java.util.List;

public interface NasaService {

    List<Meteorite> getMeteorites() throws MeteoriteServerException;

}
