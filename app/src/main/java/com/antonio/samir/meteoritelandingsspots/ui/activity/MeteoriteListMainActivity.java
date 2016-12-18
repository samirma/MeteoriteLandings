package com.antonio.samir.meteoritelandingsspots.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListView;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.MeteoriteAdapter;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorFactory;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelectorView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class MeteoriteListMainActivity extends AppCompatActivity implements MeteoriteListView,
        MeteoriteSelectorView {

    public static final String ITEM_SELECTED = "ITEM_SELECTED";

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.message)
    TextView message;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private MeteoriteListPresenter presenter;
    private StaggeredGridLayoutManager sglm;
    private MeteoriteAdapter meteoriteAdapter;
    private String selectedMeteorite;

    private ProgressDialog fetchingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meteorite_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        final boolean isLandscape = getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE;

        final MeteoriteSelector meteoriteSelector = MeteoriteSelectorFactory.getMeteoriteSelector(isLandscape, this);

        meteoriteAdapter = new MeteoriteAdapter(this, null, meteoriteSelector);
        meteoriteAdapter.setHasStableIds(true);
        recyclerView.setAdapter(meteoriteAdapter);

        int columnCount = getResources().getInteger(R.integer.list_column_count);

        sglm =
                new StaggeredGridLayoutManager(columnCount, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(sglm);

        presenter = new MeteoriteListPresenter(this);

        final String selectedMeteorite = getPreviousSelectedMeteorite(savedInstanceState);

        final boolean noMeteoriteSelected = selectedMeteorite == null;

        if (savedInstanceState == null) {
            presenter.startToRecoverMeteorites();
        }

        if (!noMeteoriteSelected) {
            meteoriteSelector.selectItemId(selectedMeteorite);
        }

    }

    private String getPreviousSelectedMeteorite(Bundle savedInstanceState) {

        String meteorite = null;

        if (savedInstanceState != null) {
            meteorite = savedInstanceState.getParcelable(ITEM_SELECTED);
        }

        if (meteorite == null) {
            meteorite = getIntent().getParcelableExtra(ITEM_SELECTED);
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
        return getBaseContext();
    }

    @Override
    public void onPreExecute() {
        if (fetchingDialog == null) {
            fetchingDialog = ProgressDialog.show(this, "", getString(R.string.load), true);
        }
    }

    @Override
    public void setMeteorites(Cursor meteorites) {
        recyclerView.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        meteoriteAdapter.swapCursor(meteorites);
        dismissDialog();
    }


    @Override
    public void errorFeitch() {
        error(getString(R.string.general_error));
    }

    @Override
    public void unableToFetch() {
        error(getString(R.string.no_network));
    }

    @Override
    public void error(final String messageString) {
        recyclerView.setVisibility(View.GONE);
        message.setVisibility(View.VISIBLE);
        message.setText(messageString);
        dismissDialog();
    }

    @Override
    public void clearList() {
        meteoriteAdapter.resetData();
    }

    @Override
    public void showDatedMessage() {

    }

    @Override
    public void hideDatedMessage() {

    }

    @Override
    public void showList() {

    }

    @Override
    public void hideList() {

    }


    /*
    MeteoriteSelectorView
     */
    @Override
    public void selectLandscape(final String meteorite) {

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

        savedInstanceState.putString(ITEM_SELECTED, selectedMeteorite);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
