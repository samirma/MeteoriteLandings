package com.antonio.samir.meteoritelandingsspots.ui.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.antonio.samir.meteoritelandingsspots.Application;
import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteRepositoryFactory;
import com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteDetailActivity;
import com.antonio.samir.meteoritelandingsspots.util.analytics.AnalyticsUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteListMainActivity.ITEM_SELECTED;

public class MeteoriteRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private List<Meteorite> mMeteorites;
    private final String mPackageName;

    public MeteoriteRemoteViewsFactory(final String packageName) {
        mPackageName = packageName;
    }

    @Override
    public void onCreate() {

        AnalyticsUtil.logEvent("Widget", "Widget started");

    }

    @Override
    public void onDataSetChanged() {

        final long identityToken = Binder.clearCallingIdentity();

        mMeteorites = MeteoriteRepositoryFactory.getMeteoriteDao(Application.getContext()).getMeteoriteOrdenedSync();

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
        RemoteViews views = new RemoteViews(mPackageName, R.layout.meteorite_widget_item);

        final Meteorite meteorite = mMeteorites.get(position);

        final String meteoriteName = meteorite.getName();
        final String year = meteorite.getYearString();

        final String idString = String.valueOf(meteorite.getId());

        views.setTextViewText(R.id.title, meteoriteName);
        views.setTextViewText(R.id.year, year);
        setLocationText(meteorite.getAddress(), views);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setRemoteContentDescription(views, meteoriteName);
        }

        final Intent intent = new Intent(Application.getContext(), MeteoriteDetailActivity.class);
        intent.putExtra(ITEM_SELECTED, idString);

        views.setOnClickFillInIntent(R.id.widget_list_item, intent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mPackageName, R.layout.meteorite_widget_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return mMeteorites.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void setLocationText(final String address, RemoteViews views) {
        String text;
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