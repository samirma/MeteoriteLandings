package com.antonio.samir.meteoritelandingsspots.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepository;
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;
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

    private MeteoriteService meteoriteService;
    private GoogleMap map;

    /**
     * Create a MeteoriteDetailFragment to show the meteorite param
     * @param meteorite
     * @return
     */
    public static MeteoriteDetailFragment newInstance(final String meteorite) {
        MeteoriteDetailFragment fragment = new MeteoriteDetailFragment();
        Bundle args = new Bundle();
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

        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);

        AppCompatActivity activity = (AppCompatActivity) getActivity();

        Toolbar toolbarView = (Toolbar) view.findViewById(R.id.toolbar);

        if (null != toolbarView) {
            activity.setSupportActionBar(toolbarView);

            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        meteoriteService = MeteoriteServiceFactory.getMeteoriteService(getActivity());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setMeteorite(meteoriteId);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        //setupMap();

    }

    public void setupMap(String meteoriteName, double lat,double  log) {
        map.clear();

        final LatLng latLng = new LatLng(lat, log);

        map.addMarker(new MarkerOptions().position(
                latLng).title(meteoriteName));

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 30));

        // Zoom in, animating the camera.
        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
    }

    public void setMeteorite(String meteoriteId) {
        this.meteoriteId = meteoriteId;

        final MeteoriteDao meteoriteDao = new MeteoriteRepository(this.getContext()).getAppDatabase().meteoriteDao();

        final Meteorite meteorite = meteoriteDao.getMeteorite(meteoriteId);

        final String meteoriteName = meteorite.getName();
        this.title.setText(meteoriteName);
        this.title.setContentDescription(meteoriteName);

        setLocationText(meteorite.getAddress(), this.location);

        final String yearString = meteorite.getYearString();
        this.year.setText(yearString);
        this.year.setContentDescription(yearString);

        final String recclass = meteorite.getRecclass();
        this.recclass.setText(recclass);
        this.recclass.setContentDescription(recclass);


        final String mass = meteorite.getMass();
        this.mass.setText(mass);
        this.mass.setContentDescription(mass);

        if (map != null) {
            final Double lat = Double.valueOf(meteorite.getReclat());
            final Double log = Double.valueOf(meteorite.getReclong());
            setupMap(meteoriteName, lat, log);
        }

        AnalyticsUtil.logEvent("Detail", String.format("%s detail", meteoriteName));


    }

    public void setLocationText(final String address, final TextView text) {
        final int visibility;
        if (StringUtils.isNotEmpty(address)) {
            text.setText(address);
            text.setContentDescription(address);
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        text.setVisibility(visibility);
    }

}
