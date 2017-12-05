package com.antonio.samir.meteoritelandingsspots.service.local;

import android.location.Address;
import android.util.Log;

import com.antonio.samir.meteoritelandingsspots.Application;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class AddressService {

    public static final String TAG = AddressService.class.getSimpleName();
    final static ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 3,
            1L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    private final MeteoriteDao mMeteoriteDao;

    public AddressService() {
        mMeteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(Application.getContext());
    }

    public interface RecoveryAddressDelegate {

        void started();

        void finished();
    }

    public void recoveryAddress(RecoveryAddressDelegate delegate) {
        final List<Meteorite> meteorites = mMeteoriteDao.getMeteoritesWithOutAddress();

        try {

            if (!meteorites.isEmpty()) {
                delegate.started();

                final int size = meteorites.size();

                for (int i = 0; i < size; i++) {
                    final Meteorite met = meteorites.get(i);
                    recoverAddress(met, met.getReclat(), met.getReclong());
                }

                delegate.finished();
            }
        } catch (Exception e) {
            Log.e(TAG, "Fail to retrieve address", e);
        }

    }

    public void recoverAddress(final Meteorite meteorite, final String recLat, final String recLong) {

        executor.execute(() -> {
            final String address = getAddress(recLat, recLong);
            meteorite.setAddress(address);
            mMeteoriteDao.update(meteorite);
            Log.i(TAG, String.format("Address for id %s recovered", meteorite.getId()));
        });

    }

    private String getAddress(String recLat, String recLong) {
        String addressString = "";
        if (StringUtils.isNoneEmpty(recLat) && StringUtils.isNoneEmpty(recLong)) {
            final Address address = com.antonio.samir.meteoritelandingsspots.service.server.GeoLocationUtil.getAddress(Double.parseDouble(recLat), Double.parseDouble(recLong), Application.getContext());
            if (address != null) {
                final List<String> finalAddress = new ArrayList<>();
                final String city = address.getLocality();
                if (StringUtils.isNoneEmpty(city)) {
                    finalAddress.add(city);
                }

                final String state = address.getAdminArea();
                if (StringUtils.isNoneEmpty(state)) {
                    finalAddress.add(state);
                }

                final String countryName = address.getCountryName();
                if (StringUtils.isNoneEmpty(countryName)) {
                    finalAddress.add(countryName);
                }

                addressString = StringUtils.join(finalAddress, ", ");

            }
        }

        return addressString;
    }
}
