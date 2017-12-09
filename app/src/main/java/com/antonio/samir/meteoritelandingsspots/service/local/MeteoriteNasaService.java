package com.antonio.samir.meteoritelandingsspots.service.local;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.location.Location;
import android.support.annotation.Nullable;

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

    private final GPSTracker mGpsTracker;
    private final Context mContext;


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

        final LiveData<List<Meteorite>> liveData = meteoriteDao.getMeteoriteOrdened();

        liveData.observeForever(new Observer<List<Meteorite>>() {

            @Override
            public void onChanged(@Nullable List<Meteorite> meteorites) {

                if (meteorites.isEmpty()) {
                    //If it is empty so load the data from internet
                    final NasaService nasaService = NasaServiceFactory.getNasaService();
                    new MeteoriteNasaAsyncTaskService(nasaService, meteoriteDao).execute();
                } else {
                    //Is not empty so remove the observer
                    list.setValue(meteorites);
                    liveData.removeObserver(this);
                }

                if (gpsEnabled) {
                    final MutableLiveData<Location> mGpsTrackerLocation = mGpsTracker.getLocation();
                    mGpsTrackerLocation.observeForever(new Observer<Location>() {
                        @Override
                        public void onChanged(@Nullable Location location) {
                            if (location != null) {
                                final double latitude = location.getLatitude();
                                final double longitude = location.getLongitude();
                                //String sortOrder = String.format("ABS(reclat - %s ) + ABS(reclong - %s) ASC", latitude, longitude);

                                final SortedSet<Meteorite> sortedSet = new TreeSet<>((m1, m2) -> {
                                    final Double distance1 = m1.distance(latitude, longitude);
                                    final Double distance2 = m2.distance(latitude, longitude);
                                    return (distance1 > 0 && distance2 > 0) ? distance1.compareTo(distance2) : 0;
                                });

                                sortedSet.addAll(meteorites);

                                final ArrayList<Meteorite> value = new ArrayList<>(sortedSet);
                                list.setValue(value);

                                mGpsTrackerLocation.removeObserver(this);
                                mGpsTracker.stopUpdates();

                            }
                        }
                    });

                }

            }
        });

        return list;

    }

}
