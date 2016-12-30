package com.antonio.samir.meteoritelandingsspots.service;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServerException;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteDetailActivity;
import com.antonio.samir.meteoritelandingsspots.util.NetworkUtil;

import org.junit.Before;
import org.junit.Rule;
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

    @Rule
    public ActivityTestRule<MeteoriteDetailActivity> mActivityTestRule = new ActivityTestRule<>(MeteoriteDetailActivity.class);
    private MeteoriteDetailActivity activity;


    @Before
    public void init() {
        meteorite = new MeteoriteServiceTest();
        activity = mActivityTestRule.getActivity();
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

        final MeteoriteService MeteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(activity);

        MeteoriteFetchService.getMeteorites(meteoriteTestDelegate, mActivity);

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
        final MeteoriteService MeteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(activity);
        MeteoriteFetchService.getMeteorites(meteoriteTestDelegate, mActivity);

        signal.await(TIMEOUT, TimeUnit.SECONDS);

        assertTrue("onPreExecuted not should be executed", !onPreExecuted.get());
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
        final MeteoriteService MeteoriteFetchService = MeteoriteServiceFactory.getMeteoriteService(activity);
        MeteoriteFetchService.getMeteorites(meteoriteTestDelegate, mActivity);

        signal.await(TIMEOUT, TimeUnit.SECONDS);

        assertTrue("onPreExecuted should be executed", onPreExecuted.get());
        assertTrue("setMeteorites should be executed", onPostExecute.get());
        assertTrue("unableToFetch should not be executed", !unableToFetch.get());
        assertTrue("fail should not be executed", !fail.get());


    }
}
