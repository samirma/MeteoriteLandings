package com.antonio.samir.meteoritelandingsspots.service.server;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import java.util.List;

public class MeteoriteServerResult {
    private List<Meteorite> meteorites;
    private MeteoriteServerException exception;

    public void setMeteorites(List<Meteorite> meteorites) {
        this.meteorites = meteorites;
    }

    public void setException(MeteoriteServerException exception) {
        this.exception = exception;
    }

    public List<Meteorite> getMeteorites() throws MeteoriteServerException {
        if (exception != null) {
            throw exception;
        }
        return meteorites;
    }

    public MeteoriteServerException getException() {
        return exception;
    }
}
