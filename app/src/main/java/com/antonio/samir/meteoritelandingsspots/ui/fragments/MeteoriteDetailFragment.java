package com.antonio.samir.meteoritelandingsspots.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.service.server.AddressService;
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

import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.MASS;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAME;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECCLASS;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECLAT;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECLONG;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.YEAR;

public class MeteoriteDetailFragment extends Fragment implements OnMapReadyCallback {

    public static final String METEORITE = "METEORITE";
    public static final String TAG = MeteoriteDetailFragment.class.getSimpleName();
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
    private double lat;
    private double log;
    private String meteoriteName;
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

        setMeteorite(meteoriteId);

        return view;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        setupMap();

    }

    public void setupMap() {
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

        final Cursor cursor = meteoriteService.getMeteoriteById(meteoriteId);

        meteoriteName = cursor.getString(cursor.getColumnIndex(NAME));

        final AddressService addressService = new AddressService(getActivity().getContentResolver());

        final String address = addressService.getAddressFromId(meteoriteId);

        final String year = cursor.getString(cursor.getColumnIndex(YEAR));

        final String recclass = cursor.getString(cursor.getColumnIndex(RECCLASS));

        final String mass = cursor.getString(cursor.getColumnIndex(MASS));

        lat = cursor.getDouble(cursor.getColumnIndex(RECLAT));

        log = cursor.getDouble(cursor.getColumnIndex(RECLONG));

        this.title.setText(meteoriteName);

        this.location.setText(address);
        setLocationText(address, this.location);

        this.year.setText(year);
        this.recclass.setText(recclass);
        this.mass.setText(mass);


        this.title.setContentDescription(meteoriteName);
        this.location.setContentDescription(address);
        this.year.setContentDescription(year);
        this.recclass.setContentDescription(recclass);
        this.mass.setContentDescription(mass);

        cursor.close();

        if (map != null) {
            setupMap();
        }

        AnalyticsUtil.logEvent("Detail", String.format("%s detail", meteoriteName));


    }

    public void setLocationText(final String address, final TextView text) {
        final int visibility;
        if (StringUtils.isNotEmpty(address)) {
            text.setText(address);
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        text.setVisibility(visibility);
    }

}
