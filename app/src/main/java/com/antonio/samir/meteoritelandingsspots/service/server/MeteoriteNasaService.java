package com.antonio.samir.meteoritelandingsspots.service.server;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.MeteoriteNasaAsyncTaskService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaService;
import com.antonio.samir.meteoritelandingsspots.service.server.nasa.NasaServiceFactory;
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

class MeteoriteNasaService implements MeteoriteService {

    private static final String TAG = MeteoriteNasaService.class.getSimpleName();
    private final GPSTracker mGpsTracker;
    private Context mContext;


    public MeteoriteNasaService(final Context context, final GPSTracker gpsTracker) {
        mContext = context;
        mGpsTracker = gpsTracker;
    }

    @Override
    public LiveData<List<Meteorite>> getMeteorites() {

        //Return meteorites
        final MeteoriteDao meteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(mContext);

        final boolean gpsEnabled = mGpsTracker.isGPSEnabled();

        final MutableLiveData<List<Meteorite>> list = new MutableLiveData<>();

        if (gpsEnabled) {
            final MutableLiveData<Location> mGpsTrackerLocation = mGpsTracker.getLocation();
            mGpsTrackerLocation.observeForever(new Observer<Location>() {
                @Override
                public void onChanged(@Nullable final Location location) {
                    if (location != null) {
                        try {
                            final double latitude = location.getLatitude();
                            final double longitude = location.getLongitude();
                            String sortOrder = String.format("ABS(reclat - %s ) + ABS(reclong - %s) ASC", latitude, longitude);
                            final LiveData<List<Meteorite>> liveData = meteoriteDao.getMeteoriteOrdened("1 ORDER BY " + sortOrder);
                            liveData.observeForever(meteorites -> {
                                final List<Meteorite> liveDataValue = liveData.getValue();

                                SortedSet<Meteorite> sortedSet = new TreeSet<>((m1, m2) -> {
                                    final Double distance1 = ((Meteorite)m1).distance(latitude, longitude);
                                    final Double distance2 = ((Meteorite)m2).distance(latitude, longitude);
                                    return (distance1 > 0 && distance2 > 0)? distance1.compareTo(distance2):0;
                                });

                                sortedSet.addAll(liveDataValue);

                                list.setValue(new ArrayList<>(sortedSet));
                            });

                            //mGpsTrackerLocation.removeObserver(this);
                            //mGpsTracker.stopUpdates();
                        } catch (Exception e) {
                            Log.e(TAG, "GPS failed", e);
                        }
                    }
                }
            });

        }

        final LiveData<List<Meteorite>> liveData = meteoriteDao.getMeteoriteOrdened(null);

        liveData.observeForever(new Observer<List<Meteorite>>() {
            @Override
            public void onChanged(@Nullable List<Meteorite> meteorites) {
                if (meteorites.isEmpty()) {
                    //If it is empty so load the data from internet
                    final NasaService nasaService = NasaServiceFactory.getNasaService(mContext);
                    new MeteoriteNasaAsyncTaskService(nasaService, meteoriteDao).execute();
                } else {
                    list.setValue(meteorites);
                    liveData.removeObserver(this);
                }
            }
        });

        return list;

    }

}
