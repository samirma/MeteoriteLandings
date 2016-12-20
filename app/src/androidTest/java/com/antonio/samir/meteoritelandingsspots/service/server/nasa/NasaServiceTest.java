package com.antonio.samir.meteoritelandingsspots.service.server.nasa;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class NasaServiceTest {

    private Context mContext;
    private NasaService nasaService;

    @Before
    public void init(){
        mContext = InstrumentationRegistry.getTargetContext();
        nasaService = NasaServiceFactory.getNasaService(mContext);
    }

    @Test
    public void getMeteorites() throws Exception {
        final List<Meteorite> meteorites = nasaService.getMeteorites();

        Assert.assertTrue((meteorites!=null) && (!meteorites.isEmpty()));

    }

}