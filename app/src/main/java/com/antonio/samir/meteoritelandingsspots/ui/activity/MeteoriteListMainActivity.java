package com.antonio.samir.meteoritelandingsspots.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListView;
import com.antonio.samir.meteoritelandingsspots.ui.fragments.MeteoriteDetailFragment;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.MeteoriteAdapter;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorFactory;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorView;

import org.apache.commons.lang3.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MeteoriteListMainActivity extends AppCompatActivity implements MeteoriteListView,
        MeteoriteSelectorView {

    public static final String ITEM_SELECTED = "ITEM_SELECTED";
    public static final String SCROLL_POSITION = "SCROLL_POSITION";

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.message)
    TextView message;

    Toolbar toolbar;

    private MeteoriteListPresenter presenter;
    private GridLayoutManager sglm;
    private MeteoriteAdapter meteoriteAdapter;
    private String selectedMeteorite;

    private ProgressDialog fetchingDialog;
    private FrameLayout frameLayout;
    private MeteoriteDetailFragment meteoriteDetailFragment;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteorite_list);

        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (null != toolbar) {
            setSupportActionBar(toolbar);
        }


        final boolean isLandscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        final MeteoriteSelector meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(isLandscape, this);

        meteoriteAdapter = new MeteoriteAdapter(this, null, meteoriteSelector);
        meteoriteAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(meteoriteAdapter);

        frameLayout = (FrameLayout) findViewById(R.id.fragment);

        int columnCount = getResources().getInteger(R.integer.list_column_count);

        sglm =
                new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(sglm);

        presenter = new MeteoriteListPresenter(this);

        final String selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState);

        if (StringUtils.isNoneBlank(selectedMeteorite)) {
            meteoriteSelector.selectItemId(selectedMeteorite);
        }

        this.savedInstanceState = savedInstanceState;


    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.startToRecoverMeteorites();

    }

    private String getPreviousSelectedMeteorite(Bundle savedInstanceState) {

        String meteorite = null;

        if (savedInstanceState != null) {
            meteorite = savedInstanceState.getString(ITEM_SELECTED);
        }

        final Bundle extras = getIntent().getExtras();
        if (meteorite == null && extras != null) {
            meteorite = extras.getString(ITEM_SELECTED);
        }

        return meteorite;
    }



    private void dismissDialog() {
        if (fetchingDialog != null) {
            fetchingDialog.dismiss();
            fetchingDialog = null;
        }
    }

    /*
    MeteoriteListView
     */
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onPreExecute() {
        if (fetchingDialog == null) {
            fetchingDialog = ProgressDialog.show(this, "", getString(R.string.load), true);
        }
    }

    @Override
    public void setMeteorites(Cursor meteorites) {
        mRecyclerView.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        meteoriteAdapter.swapCursor(meteorites);
        dismissDialog();

        if (savedInstanceState != null) {
            final int anInt = savedInstanceState.getInt(SCROLL_POSITION, -1);
            if (anInt > 0) {
                sglm.scrollToPosition(anInt);
            }
        }
    }

    @Override
    public void unableToFetch() {
        error(getString(R.string.no_network));
    }

    @Override
    public void error(final String messageString) {
        mRecyclerView.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        message.setText(messageString);
        dismissDialog();
    }

    @Override
    public void clearList() {
        meteoriteAdapter.resetData();
    }


    @Override
    public void showList() {
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideList() {
        mRecyclerView.setVisibility(View.GONE);
    }


    /*
    MeteoriteSelectorView
     */
    @Override
    public void selectLandscape(final String meteorite) {

        if (selectedMeteorite == null) {
            frameLayout.setVisibility(View.VISIBLE);
            sglm = new GridLayoutManager(this, 1);
            mRecyclerView.setLayoutManager(sglm);

            final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            meteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite);
            fragmentTransaction.add(R.id.fragment, meteoriteDetailFragment);
            fragmentTransaction.commit();

        } else {
            meteoriteDetailFragment.setMeteorite(meteorite);
        }

        selectedMeteorite = meteorite;
    }

    @Override
    public void selectPortrait(final String meteorite) {
        selectedMeteorite = meteorite;
        final Intent intent = new Intent(this, MeteoriteDetailActivity.class);
        intent.putExtra(ITEM_SELECTED, meteorite);
        startActivity(intent);
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (selectedMeteorite != null) {
            savedInstanceState.putString(ITEM_SELECTED, selectedMeteorite);
        }

        int lastFirstVisiblePosition = sglm.findFirstCompletelyVisibleItemPosition();
        savedInstanceState.putInt(SCROLL_POSITION, lastFirstVisiblePosition);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    @Override
    protected void onStop() {
        super.onStop();
        final MeteoriteListView view = this;
        presenter.removeView(view);
    }
}
