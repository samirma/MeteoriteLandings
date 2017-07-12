package com.antonio.samir.meteoritelandingsspots.ui.widget;

import com.antonio.samir.meteoritelandingsspots.Application;
import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListView;
import com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteDetailActivity;
import com.antonio.samir.meteoritelandingsspots.util.analytics.AnalyticsUtil;

import org.apache.commons.lang3.StringUtils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.List;

import static com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteListMainActivity.ITEM_SELECTED;

/**
 * Created by samir.antonio on 12/07/2017.
 */

public class MeteoriteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory, MeteoriteListView {
    private List<Meteorite> mMeteorites;
    private MeteoriteListPresenter mPresenter;
    private String mPackageName;

    public MeteoriteRemoteViewsFactory(final String packageName) {
        mPackageName = packageName;
    }

    @Override
    public void onCreate() {

        AnalyticsUtil.logEvent("Widget", "Widget started");

        mPresenter = new MeteoriteListPresenter(this);

        mPresenter.startToRecoverMeteorites();

    }

    @Override
    public void onDataSetChanged() {

        final long identityToken = Binder.clearCallingIdentity();


        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mMeteorites != null) {
            mMeteorites = null;
        }
    }

    @Override
    public int getCount() {
        return mMeteorites == null ? 0 : mMeteorites.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mPackageName, R.layout.meteorite_detail_item);

        final Meteorite meteorite = new Meteorite();

        final String meteoriteName = meteorite.getName();
        final String year = meteorite.getYearString();

        final String idString = meteorite.getId();

        views.setTextViewText(R.id.title, meteoriteName);
        views.setTextViewText(R.id.year, year);
        setLocationText(meteorite.getAddress(), views);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setRemoteContentDescription(views, meteoriteName);
        }

        final Intent intent = new Intent(getContext(), MeteoriteDetailActivity.class);
        intent.putExtra(ITEM_SELECTED, idString);

        views.setOnClickFillInIntent(R.id.widget_list_item, intent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mPackageName, R.layout.meteorite_detail_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(mMeteorites.get(position).getId());
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public Context getContext() {
        return Application.getContext();
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void setMeteorites(final List<Meteorite> result) {
        mMeteorites = result;
    }

    @Override
    public void unableToFetch() {

    }

    @Override
    public void error(final String s) {

    }

    @Override
    public void clearList() {

    }

    @Override
    public void showList() {

    }

    @Override
    public void hideList() {

    }

    public void setLocationText(final String address, RemoteViews views) {
        String text = "";
        if (StringUtils.isNotEmpty(address)) {
            text = address;
        } else {
            text = "";
        }
        views.setTextViewText(R.id.location, text);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_list_item, description);
    }
}