package com.antonio.samir.meteoritelandingsspots.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAME;
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

        final MeteoriteService meteoriteService = MeteoriteServiceFactory.getMeteoriteService(getActivity());

        final Cursor cursor = meteoriteService.getMeteoriteById(meteoriteId);

        final String meteoriteName = cursor.getString(cursor.getColumnIndex(NAME));
        final String location = cursor.getString(cursor.getColumnIndex(NAME));
        final String year = cursor.getString(cursor.getColumnIndex(YEAR));

        title.setText(meteoriteName);
        this.location.setText(location);
        this.year.setText(year);

        return view;
    }



}
