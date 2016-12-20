package com.antonio.samir.meteoritelandingsspots.service.server;

import android.database.Cursor;


/**
 * Interface used to handle with the callBack photo command
 */
public interface MeteoriteServiceDelegate {
    void onPreExecute();

    void setPhotos(Cursor result);

    void fail(MeteoriteServerException e);

    void reReseted();

    void unableToFetch();
}
