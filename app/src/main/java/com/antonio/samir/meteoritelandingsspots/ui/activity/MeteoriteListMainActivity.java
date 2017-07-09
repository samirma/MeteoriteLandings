package com.antonio.samir.meteoritelandingsspots.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListView;
import com.antonio.samir.meteoritelandingsspots.ui.fragments.MeteoriteDetailFragment;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.MeteoriteAdapter;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.ViewHolderMeteorite;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorFactory;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorView;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MeteoriteListMainActivity extends AppCompatActivity implements MeteoriteListView,
        MeteoriteSelectorView {

    public static final String ITEM_SELECTED = "ITEM_SELECTED";
    public static final String SCROLL_POSITION = "SCROLL_POSITION";
    public static final String TAG = MeteoriteListMainActivity.class.getSimpleName();

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
    private boolean mIsLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteorite_list);

        ButterKnife.bind(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }


        mIsLandscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        final MeteoriteSelector meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(mIsLandscape, this);

        meteoriteAdapter = new MeteoriteAdapter(this, meteoriteSelector);
        meteoriteAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(meteoriteAdapter);

        frameLayout = (FrameLayout) findViewById(R.id.fragment);

        setupGridLayout();

        presenter = new MeteoriteListPresenter(this);

        final String selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState);

        if (StringUtils.isNoneBlank(selectedMeteorite)) {
            meteoriteSelector.selectItemId(selectedMeteorite);
        }

        this.savedInstanceState = savedInstanceState;

        presenter.startToRecoverMeteorites();


    }

    private void setupGridLayout() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);

        sglm =
                new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(sglm);
    }

    private String getPreviousSelectedMeteorite(Bundle savedInstanceState) {

        String meteorite = null;

        if (savedInstanceState != null) {
            meteorite = savedInstanceState.getString(ITEM_SELECTED);
        }

        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        boolean isRedeliver = savedInstanceState != null || (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0;
        if (meteorite == null && extras != null && !isRedeliver) {
            meteorite = extras.getString(ITEM_SELECTED);
        }

        Log.i(TAG, "isRedeliver: " + isRedeliver);

        return meteorite;
    }



    private void dismissDialog() {
        try {
            if (fetchingDialog != null && fetchingDialog.isShowing()) {
                fetchingDialog.dismiss();
                fetchingDialog = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
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
        try {
            if (fetchingDialog == null) {
                fetchingDialog = ProgressDialog.show(this, "", getString(R.string.load), true);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void setMeteorites(List<Meteorite> meteorites) {
        mRecyclerView.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        meteoriteAdapter.setData(meteorites);
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

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (selectedMeteorite == null) {
            frameLayout.setVisibility(View.VISIBLE);
            sglm = new GridLayoutManager(this, 1);
            mRecyclerView.setLayoutManager(sglm);
        }
        fragmentTransaction = fragmentTransaction.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit);

        meteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite);
        fragmentTransaction.replace(R.id.fragment, meteoriteDetailFragment);
        fragmentTransaction.commit();

        selectedMeteorite = meteorite;

        meteoriteAdapter.setSelectedMeteorite(selectedMeteorite);

    }

    @Override
    public void selectPortrait(final String meteorite) {

        final Intent intent = new Intent(this, MeteoriteDetailActivity.class);
        intent.putExtra(ITEM_SELECTED, meteorite);

        final ViewHolderMeteorite viewHolderMeteorite = meteoriteAdapter.getmViewHolderMeteorite();
        if (viewHolderMeteorite != null) {

            final Pair<View, String> container = Pair.create((View) viewHolderMeteorite.cardView, "cardView");
            Pair<View, String> p1 = Pair.create((View)viewHolderMeteorite.name, "title");

            final ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, container);

            startActivity(intent, options.toBundle());

        } else {
            startActivity(intent);
        }

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
    public void onBackPressed() {
        if (selectedMeteorite != null && mIsLandscape) {
            selectedMeteorite = null;
            meteoriteAdapter.setSelectedMeteorite(selectedMeteorite);
            setupGridLayout();
            frameLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
