package com.antonio.samir.meteoritelandingsspots.service;


import android.database.Cursor;

import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceDelegate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class MeteoriteTestDelegate implements MeteoriteServiceDelegate {

    final AtomicBoolean onPreExecuted;
    final AtomicBoolean onPostExecute;
    final AtomicBoolean unableToFetch;
    final AtomicBoolean fail;
    final CountDownLatch signal;

    public MeteoriteTestDelegate(AtomicBoolean onPreExecuted, AtomicBoolean onPostExecute, AtomicBoolean unableToFetch, AtomicBoolean fail, CountDownLatch signal) {
        this.onPreExecuted = onPreExecuted;
        this.onPostExecute = onPostExecute;
        this.unableToFetch = unableToFetch;
        this.fail = fail;
        this.signal = signal;
    }

    @Override
    public void onPreExecute() {
        onPreExecuted.set(true);
    }

    @Override
    public void setCursor(Cursor result) {
        onPostExecute.set(true);
        signal.countDown();
    }

    @Override
    public void fail(MeteoriteServerException e) {
        fail.set(true);
        signal.countDown();
    }

    @Override
    public void reReseted() {

    }

    @Override
    public void unableToFetch() {
        unableToFetch.set(true);
        signal.countDown();
    }

}