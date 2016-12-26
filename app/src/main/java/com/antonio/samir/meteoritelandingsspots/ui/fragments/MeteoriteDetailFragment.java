package com.antonio.samir.meteoritelandingsspots.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.service.server.AddressService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.MASS;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAME;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAMETYPE;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECCLASS;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.YEAR;

public class MeteoriteDetailFragment extends Fragment {

    public static final String METEORITE = "METEORITE";
    public static final String TAG = MeteoriteDetailFragment.class.getSimpleName();
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.location)
    TextView location;
    @BindView(R.id.year)
    TextView year;
    @BindView(R.id.content_detail)
    LinearLayout contentDetail;

    @BindView(R.id.mass)
    TextView mass;


    @BindView(R.id.nametype)
    TextView nametype;

    @BindView(R.id.recclass)
    TextView recclass;

    private String meteoriteId;

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

        final MeteoriteService meteoriteService = MeteoriteServiceFactory.getMeteoriteService((AppCompatActivity) getActivity());

        final Cursor cursor = meteoriteService.getMeteoriteById(meteoriteId);

        final String meteoriteName = cursor.getString(cursor.getColumnIndex(NAME));

        final AddressService addressService = new AddressService(getActivity().getContentResolver());

        final String address = addressService.getAddressFromId(meteoriteId);

        final String year = cursor.getString(cursor.getColumnIndex(YEAR));

        final String nametype = cursor.getString(cursor.getColumnIndex(NAMETYPE));

        final String recclass = cursor.getString(cursor.getColumnIndex(RECCLASS));

        final String mass = cursor.getString(cursor.getColumnIndex(MASS));

        this.title.setText(meteoriteName);
        this.location.setText(address);
        this.year.setText(year);
        this.recclass.setText(recclass);
        this.mass.setText(mass);
        this.nametype.setText(nametype);


        this.title.setContentDescription(meteoriteName);
        this.location.setContentDescription(address);
        this.year.setContentDescription(year);
        this.recclass.setContentDescription(recclass);
        this.mass.setContentDescription(mass);
        this.nametype.setContentDescription(nametype);

        cursor.close();

        return view;
    }



}
