package com.antonio.samir.meteoritelandingsspots.service.server;

import android.database.Cursor;

public interface MeteoriteService {

    void getMeteorites(MeteoriteServiceDelegate delegate);

    boolean isDated();

    void remove();

    Cursor getMeteoriteById(String meteoriteId);
}
