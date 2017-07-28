package com.antonio.samir.meteoritelandingsspots.service.server;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import java.util.List;


/**
 * Interface used to handle with the callBack photo command
 */
public interface MeteoriteServiceDelegate {
    void onPreExecute();

    void setMeteorites(List<Meteorite> result);

    void fail(MeteoriteServerException e);

    void reReseted();

    void unableToFetch();
}
