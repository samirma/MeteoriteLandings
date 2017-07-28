package com.antonio.samir.meteoritelandingsspots.ui.activity;

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
    TextView mMessage;

    Toolbar mToolbar;

    private MeteoriteListPresenter mPresenter;
    private GridLayoutManager mSglm;
    private MeteoriteAdapter mMeteoriteAdapter;
    private String mSelectedMeteorite;

    private ProgressDialog mProgressDialog;
    private FrameLayout mFrameLayout;
    private MeteoriteDetailFragment mMeteoriteDetailFragment;
    private Bundle mSavedInstanceState;
    private boolean mIsLandscape;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteorite_list);

        ButterKnife.bind(this);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }


        mIsLandscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        final MeteoriteSelector meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(mIsLandscape, this);

        mMeteoriteAdapter = new MeteoriteAdapter(this, meteoriteSelector);
        mMeteoriteAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mMeteoriteAdapter);

        mFrameLayout = (FrameLayout) findViewById(R.id.fragment);

        setupGridLayout();

        mPresenter = new MeteoriteListPresenter(this);

        final String selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState);

        if (StringUtils.isNoneBlank(selectedMeteorite)) {
            meteoriteSelector.selectItemId(selectedMeteorite);
        }

        this.mSavedInstanceState = savedInstanceState;

        mPresenter.startToRecoverMeteorites();


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

    @Override
    public void onPreExecute() {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(this, "", getString(R.string.load), true);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void setMeteorites(final List<Meteorite> meteorites) {
        mRecyclerView.setVisibility(View.VISIBLE);
        mMessage.setVisibility(View.GONE);
        mMeteoriteAdapter.setData(meteorites);
        mMeteoriteAdapter.notifyDataSetChanged();
        dismissDialog();

        if (mSavedInstanceState != null) {
            final int anInt = mSavedInstanceState.getInt(SCROLL_POSITION, -1);
            if (anInt > 0) {
                mSglm.scrollToPosition(anInt);
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
        mMessage.setVisibility(View.VISIBLE);
        mMessage.setText(messageString);
        dismissDialog();
    }

    @Override
    public void clearList() {
        mMeteoriteAdapter.resetData();
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

        if (mSelectedMeteorite == null) {
            mFrameLayout.setVisibility(View.VISIBLE);
            mSglm = new GridLayoutManager(this, 1);
            mRecyclerView.setLayoutManager(mSglm);
        }
        fragmentTransaction = fragmentTransaction.setCustomAnimations(
                R.anim.fragment_slide_left_enter,
                R.anim.fragment_slide_left_exit);

        mMeteoriteDetailFragment = MeteoriteDetailFragment.newInstance(meteorite);
        fragmentTransaction.replace(R.id.fragment, mMeteoriteDetailFragment);
        fragmentTransaction.commit();

        mSelectedMeteorite = meteorite;

        mMeteoriteAdapter.setSelectedMeteorite(mSelectedMeteorite);

    }

    @Override
    public void selectPortrait(final String meteorite) {

        final Intent intent = new Intent(this, MeteoriteDetailActivity.class);
        intent.putExtra(ITEM_SELECTED, meteorite);

        final ViewHolderMeteorite viewHolderMeteorite = mMeteoriteAdapter.getmViewHolderMeteorite();
        if (viewHolderMeteorite != null) {

            final Pair<View, String> container = Pair.create((View) viewHolderMeteorite.mCardview, "mCardview");
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

    private void dismissDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
}
