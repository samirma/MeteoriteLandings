package com.antonio.samir.meteoritelandingsspots.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeteoriteDetailFragment extends Fragment {

    public static final String METEORITE = "METEORITE";
    public static final String TAG = MeteoriteDetailFragment.class.getSimpleName();

    private Meteorite meteorite;

    @BindView(R.id.image)
    ImageView imageView;

    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.content_detail)
    LinearLayout contentDetail;

    /**
     * Create a MeteoriteDetailFragment to show the meteorite param
     * @param meteorite
     * @return
     */
    public static MeteoriteDetailFragment newInstance(final Meteorite meteorite) {
        MeteoriteDetailFragment fragment = new MeteoriteDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(METEORITE, meteorite);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //Recovering the meteorite to work on it
            meteorite = getArguments().getParcelable(METEORITE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_meteorite_detail, container, false);

        ButterKnife.bind(this, view);

        title.setText(meteorite.getName());

        return view;
    }



}
