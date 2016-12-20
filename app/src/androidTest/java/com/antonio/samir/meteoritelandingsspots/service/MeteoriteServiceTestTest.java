package com.antonio.samir.meteoritelandingsspots.service;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class MeteoriteServiceTestTest {

    public static final int TIMEOUT = 1000;
    private static MeteoriteServiceTest meteorite;
    private static Context context;

    @Before
    public void init() {
        meteorite = new MeteoriteServiceTest();
        context = InstrumentationRegistry.getTargetContext();
        NasaServiceFactory.setNasaService(meteorite);
    }


    @Test
    public void failTest() throws Exception {

        final AtomicBoolean onPreExecuted = new AtomicBoolean(false);
        final AtomicBoolean onPostExecute = new AtomicBoolean(false);
        final AtomicBoolean unableToFetch = new AtomicBoolean(false);
        final AtomicBoolean fail = new AtomicBoolean(false);
        final CountDownLatch signal = new CountDownLatch(1);

        NetworkUtil.setConnectivity(true);

        meteorite.setError(new MeteoriteServerException(new Exception("Some unexpected error")));

        final MeteoriteTestDelegate meteoriteTestDelegate = new MeteoriteTestDelegate(onPreExecuted, onPostExecute, unableToFetch, fail, signal);

        final MeteoriteService MeteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(context);

        MeteoriteFetchService.getMeteorites(meteoriteTestDelegate);

        signal.await(TIMEOUT, TimeUnit.SECONDS);

        assertTrue("onPreExecuted should be executed", onPreExecuted.get());
        assertTrue("setMeteorites should be executed", !onPostExecute.get());
        assertTrue("unableToFetch should not be executed", !unableToFetch.get());
        assertTrue("fail should be executed", fail.get());

    }

    @Test
    public void MeteoriteNoNetWork() throws Exception {

        final AtomicBoolean onPreExecuted = new AtomicBoolean(false);
        final AtomicBoolean onPostExecute = new AtomicBoolean(false);
        final AtomicBoolean unableToFetch = new AtomicBoolean(false);
        final AtomicBoolean fail = new AtomicBoolean(false);
        final CountDownLatch signal = new CountDownLatch(1);
        NetworkUtil.setConnectivity(false);


        final MeteoriteTestDelegate meteoriteTestDelegate = new MeteoriteTestDelegate(onPreExecuted, onPostExecute, unableToFetch, fail, signal);
        final MeteoriteService MeteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(context);
        MeteoriteFetchService.getMeteorites(meteoriteTestDelegate);

        signal.await(TIMEOUT, TimeUnit.SECONDS);

        assertTrue("onPreExecuted should be executed", !onPreExecuted.get());
        assertTrue("setMeteorites should be executed", !onPostExecute.get());
        assertTrue("unableToFetch should not be executed", unableToFetch.get());
        assertTrue("fail should not be executed", !fail.get());

    }

    @Test
    public void MeteoriteRetrivedWithSuccess() throws Exception {

        final AtomicBoolean onPreExecuted = new AtomicBoolean(false);
        final AtomicBoolean onPostExecute = new AtomicBoolean(false);
        final AtomicBoolean unableToFetch = new AtomicBoolean(false);
        final AtomicBoolean fail = new AtomicBoolean(false);
        final CountDownLatch signal = new CountDownLatch(1);
        NetworkUtil.setConnectivity(true);

        final MeteoriteTestDelegate meteoriteTestDelegate = new MeteoriteTestDelegate(onPreExecuted, onPostExecute, unableToFetch, fail, signal);
        final MeteoriteService MeteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(context);
        MeteoriteFetchService.getMeteorites(meteoriteTestDelegate);

        signal.await(TIMEOUT, TimeUnit.SECONDS);

        assertTrue("onPreExecuted should be executed", onPreExecuted.get());
        assertTrue("setMeteorites should be executed", onPostExecute.get());
        assertTrue("unableToFetch should not be executed", !unableToFetch.get());
        assertTrue("fail should not be executed", !fail.get());


    }
}
