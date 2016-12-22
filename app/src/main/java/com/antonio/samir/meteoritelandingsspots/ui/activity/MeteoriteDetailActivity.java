package com.antonio.samir.meteoritelandingsspots.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.ui.fragments.MeteoriteDetailFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;
import static com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteListMainActivity.ITEM_SELECTED;

public class MeteoriteDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String selectedMeteorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteorite_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final boolean isLandscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState);

        if (isLandscape) {
            //If MeteoriteDetailActivity is created on landscape so return to MeteoriteListMainActivity
            final Intent intent = new Intent(this, MeteoriteListMainActivity.class);
            intent.putExtra(ITEM_SELECTED, selectedMeteorite);
            startActivity(intent);
        } else {
            //If MeteoriteDetailActivity is created on portrait so load the fragment
            final MeteoriteDetailFragment meteoriteDetailFragment = MeteoriteDetailFragment.newInstance(selectedMeteorite);
            final FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.add(R.id.frag_detail, meteoriteDetailFragment);
            fragmentTransaction.commit();
        }

    }

    private String getPreviousSelectedMeteorite(Bundle savedInstanceState) {
        String meteorite = getIntent().getExtras().getString(ITEM_SELECTED);
        if (meteorite == null && savedInstanceState != null) {
            meteorite = savedInstanceState.getString(ITEM_SELECTED);
        }
        return meteorite;
    }

    /**
     * Activity cicle life saving state
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        //Saving the selected meteorite
        savedInstanceState.putString(ITEM_SELECTED, selectedMeteorite);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

}
