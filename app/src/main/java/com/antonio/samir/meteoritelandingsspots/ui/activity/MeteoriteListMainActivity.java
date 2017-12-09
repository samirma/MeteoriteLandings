package com.antonio.samir.meteoritelandingsspots.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import com.antonio.samir.meteoritelandingsspots.service.local.AddressService;
import com.antonio.samir.meteoritelandingsspots.ui.fragments.MeteoriteDetailFragment;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.MeteoriteAdapter;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.ViewHolderMeteorite;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorFactory;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorView;
import com.antonio.samir.meteoritelandingsspots.ui.viewmodel.MeteoriteViewModel;
import com.antonio.samir.meteoritelandingsspots.util.GPSTracker;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MeteoriteListMainActivity extends AppCompatActivity implements MeteoriteListView,
        MeteoriteSelectorView, GPSTracker.GPSTrackerDelegate {

    public static final String ITEM_SELECTED = "ITEM_SELECTED";
    public static final String SCROLL_POSITION = "SCROLL_POSITION";
    public static final String TAG = MeteoriteListMainActivity.class.getSimpleName();
    public static final int LOCATION_REQUEST_CODE = 11111;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.message)
    TextView mMessage;

    @BindView(R.id.status)
    TextView mStatus;

    Toolbar mToolbar;

    private MeteoriteListPresenter mPresenter;
    private GridLayoutManager mSglm;
    private MeteoriteAdapter mMeteoriteAdapter;
    private String mSelectedMeteorite;

    private ProgressDialog mProgressDialog;
    private FrameLayout mFrameLayout;
    private Bundle mSavedInstanceState;
    private boolean mIsLandscape;
    private MeteoriteViewModel mMeteoriteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteorite_list);

        ButterKnife.bind(this);

        mToolbar = findViewById(R.id.toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }

        mMeteoriteViewModel = ViewModelProviders.of(this).get(MeteoriteViewModel.class);

        mPresenter = mMeteoriteViewModel.getPresenter();

        mIsLandscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        final MeteoriteSelector meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(mIsLandscape, this);

        mMeteoriteAdapter = new MeteoriteAdapter(this, meteoriteSelector, mPresenter);
        mMeteoriteAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mMeteoriteAdapter);

        mFrameLayout = findViewById(R.id.fragment);

        setupGridLayout();

        final String selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState);

        if (StringUtils.isNoneBlank(selectedMeteorite)) {
            meteoriteSelector.selectItemId(selectedMeteorite);
        }

        this.mSavedInstanceState = savedInstanceState;

        mPresenter.attachView(this);

        mPresenter.getRecoveryAddress().observe(this, status -> {
            if (status == null || status == AddressService.Status.DONE) {
                hideAddressLoading();
            } else if (status == AddressService.Status.LOADING) {
                showAddressLoading();
            }
        });

        getMeteorites();


    }

    private void showAddressLoading() {
        mStatus.setVisibility(View.VISIBLE);
    }

    private void hideAddressLoading() {
        mStatus.setVisibility(View.GONE);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        if (mSelectedMeteorite != null) {
            savedInstanceState.putString(ITEM_SELECTED, mSelectedMeteorite);
        }

        int lastFirstVisiblePosition = mSglm.findFirstCompletelyVisibleItemPosition();
        savedInstanceState.putInt(SCROLL_POSITION, lastFirstVisiblePosition);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    /*
    MeteoriteListView
     */
    @Override
    public Context getContext() {
        return this;
    }

    public void getMeteorites() {

        final LiveData<List<Meteorite>> meteorites = mMeteoriteViewModel.getMeteorites();

        meteorites.observe(this, meteorites1 -> {
            if (meteorites1 != null && !meteorites1.isEmpty()) {
                mMeteoriteAdapter.setData(meteorites1);
                mMeteoriteAdapter.notifyDataSetChanged();

                if (mSavedInstanceState != null) {
                    final int anInt = mSavedInstanceState.getInt(SCROLL_POSITION, -1);
                    if (anInt > 0) {
                        mSglm.scrollToPosition(anInt);
                    }
                }
            }
        });

    }

    @Override
    public void unableToFetch() {
        error(getString(R.string.no_network));
    }

    public void error(final String messageString) {
        mRecyclerView.setVisibility(View.GONE);
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setText(messageString);
        meteoriteLoadingStopped();
    }

    @Override
    public void hideList() {
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public GPSTracker.GPSTrackerDelegate getGPSDelegate() {
        return this;
    }


    /*
    MeteoriteSelectorView
     */
    @Override
    public void selectLandscape(final String meteorite) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (mSelectedMeteorite == null) {
            mFrameLayout.setVisibility(View.VISIBLE);
            mSglm = new GridLayoutManager(this, 1);
            mRecyclerView.setLayoutManager(mSglm);
        }
        fragmentTransaction = fragmentTransaction.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit);

        MeteoriteDetailFragment mMeteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite);
        fragmentTransaction.replace(R.id.fragment, mMeteoriteDetailFragment);
        fragmentTransaction.commit();

        mSelectedMeteorite = meteorite;

        mMeteoriteAdapter.setSelectedMeteorite(mSelectedMeteorite);

    }

    @Override
    public void selectPortrait(final String meteorite) {

        final Intent intent = new Intent(this, MeteoriteDetailActivity.class);
        intent.putExtra(ITEM_SELECTED, meteorite);

        final ViewHolderMeteorite viewHolderMeteorite = mMeteoriteAdapter.getmVieHolderMeteorite();
        if (viewHolderMeteorite != null) {

            final Pair<View, String> container = Pair.create(viewHolderMeteorite.mCardview, "mCardview");
            //Pair<View, String> p1 = Pair.create((View) viewHolderMeteorite.mName, "title");

            final ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, container);

            startActivity(intent, options.toBundle());

        } else {
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        if (mSelectedMeteorite != null && mIsLandscape) {
            mSelectedMeteorite = null;
            mMeteoriteAdapter.setSelectedMeteorite(null);
            setupGridLayout();
            mFrameLayout.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    private void setupGridLayout() {
        int columnCount = getResources().getInteger(R.integer.list_column_count);

        mSglm =
                new GridLayoutManager(this, columnCount);
        mRecyclerView.setLayoutManager(mSglm);
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

    @Override
    public void meteoriteLoadingStarted() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(this, "", getString(R.string.load), true);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void meteoriteLoadingStopped() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            for (int grantResult : grantResults) {
                boolean isPermitted = grantResult == PackageManager.PERMISSION_GRANTED;
                if (isPermitted) {
                    mPresenter.updateLocation();
                }
            }
        }
    }
}
