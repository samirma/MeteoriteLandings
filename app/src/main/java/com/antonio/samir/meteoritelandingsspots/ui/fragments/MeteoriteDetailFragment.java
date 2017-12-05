package com.antonio.samir.meteoritelandingsspots.ui.fragments;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.util.analytics.AnalyticsUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeteoriteDetailFragment extends Fragment implements OnMapReadyCallback {

    public static final String METEORITE = "METEORITE";

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.year)
    TextView year;

    @BindView(R.id.mass)
    TextView mass;

    @BindView(R.id.recclass)
    TextView recclass;

    private String meteoriteId;

    private GoogleMap mMap;

    /**
     * Create a MeteoriteDetailFragment to show the meteorite param
     * @param meteorite
     * @return
     */
    public static MeteoriteDetailFragment newInstance(final String meteorite) {
        final MeteoriteDetailFragment fragment = new MeteoriteDetailFragment();
        final Bundle args = new Bundle();
        args.putString(METEORITE, meteorite);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Recovering the meteorite to work on it
            meteoriteId = getArguments().getString(METEORITE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_meteorite_detail, container, false);

        ButterKnife.bind(this, view);

        ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);

        final AppCompatActivity activity = (AppCompatActivity) getActivity();

        final Toolbar toolbarView = (Toolbar) view.findViewById(R.id.toolbar);

        if (null != toolbarView) {
            activity.setSupportActionBar(toolbarView);

            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setMeteorite(meteoriteId);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.mMap = map;
    }

    public void setupMap(String meteoriteName, double lat,double  log) {
        mMap.clear();

        final LatLng latLng = new LatLng(lat, log);

        mMap.addMarker(new MarkerOptions().position(
                latLng).title(meteoriteName));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    public void setMeteorite(final String meteoriteId) {
        this.meteoriteId = meteoriteId;

        final MeteoriteDao meteoriteDao = MeteoriteRepositoryFactory.getMeteoriteDao(getContext());

        final LiveData<Meteorite> meteoriteLiveData = meteoriteDao.getMeteoriteById(meteoriteId);

        meteoriteLiveData.observe(this, new Observer<Meteorite>() {
            @Override
            public void onChanged(@Nullable final Meteorite meteorite) {
                initView(meteorite);
            }
        });

    }

    private void initView(final Meteorite meteorite) {
        final String meteoriteName = meteorite.getName();
        setText(this.title, meteoriteName);

        setLocationText(meteorite.getAddress(), this.location);

        final String yearString = meteorite.getYearString();
        setText(this.year, yearString);

        final String recclass = meteorite.getRecclass();
        setText(this.recclass, recclass);


        final String mass = meteorite.getMass();
        setText(this.mass, mass);

        if (mMap != null) {
            final Double lat = Double.valueOf(meteorite.getReclat());
            final Double log = Double.valueOf(meteorite.getReclong());
            setupMap(meteoriteName, lat, log);
        }

        AnalyticsUtil.logEvent("Detail", String.format("%s detail", meteoriteName));
    }

    public void setLocationText(final String address, final TextView text) {
        if (StringUtils.isNotEmpty(address)) {
            setText(text, address);
            text.setVisibility(View.VISIBLE);
        } else {
            text.setVisibility(View.GONE);
        }
    }

    private void setText(final TextView text, final String address) {
        text.setText(address);
        text.setContentDescription(address);
    }

}
