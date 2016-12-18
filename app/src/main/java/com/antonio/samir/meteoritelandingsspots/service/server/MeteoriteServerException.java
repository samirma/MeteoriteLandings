package com.antonio.samir.meteoritelandingsspots.service.server;

/**
 * Server Exception created in order to prevent the ui layer be aware the exception of service layer
 */
public class MeteoriteServerException extends Exception {

    public MeteoriteServerException(final Exception exception) {
        super(exception);
    }

    public MeteoriteServerException(final String exception) {
        super(exception);
    }

}
