package com.antonio.samir.meteoritelandingsspots.service.server;

public interface MeteoriteService {

    void getMeteorites(MeteoriteServiceDelegate delegate);

    void remove();

    String getOrderString();

}
