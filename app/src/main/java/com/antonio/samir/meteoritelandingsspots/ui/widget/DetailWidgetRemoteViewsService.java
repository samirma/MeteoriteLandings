package com.antonio.samir.meteoritelandingsspots.ui.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider;
import com.antonio.samir.meteoritelandingsspots.service.server.AddressService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteService;
import com.antonio.samir.meteoritelandingsspots.service.server.MeteoriteServiceFactory;
import com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteDetailActivity;

import org.apache.commons.lang3.StringUtils;

import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAME;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.YEAR;
import static com.antonio.samir.meteoritelandingsspots.ui.activity.MeteoriteListMainActivity.ITEM_SELECTED;

/**
 * RemoteViewsService controlling the cursor being shown in the scrollable meteorite widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DetailWidgetRemoteViewsService extends RemoteViewsService {
    private static final int ID = 0;
    public final String LOG_TAG = DetailWidgetRemoteViewsService.class.getSimpleName();
    private AddressService addressService;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor cursor = null;

            @Override
            public void onCreate() {

                addressService = new AddressService(getContentResolver());

            }

            @Override
            public void onDataSetChanged() {
                if (cursor != null) {
                    cursor.close();
                }

                final long identityToken = Binder.clearCallingIdentity();

                final MeteoriteService meteoriteService = MeteoriteServiceFactory.getMeteoriteService(getBaseContext());

                cursor = getContentResolver().query(MeteoriteProvider.Meteorites.LISTS,
                        meteoriteService.getProjection(),
                        null,
                        null,
                        meteoriteService.getOrderString());

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (cursor != null) {
                    cursor.close();
                    cursor = null;
                }
            }

            @Override
            public int getCount() {
                return cursor == null ? 0 : cursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        cursor == null || !cursor.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.meteorite_detail_item);

                final String meteoriteName = cursor.getString(cursor.getColumnIndex(NAME));
                final String year = cursor.getString(cursor.getColumnIndex(YEAR));

                final int id = cursor.getColumnIndex(MeteoriteColumns.ID);
                final String idString = cursor.getString(id);

                views.setTextViewText(R.id.title, meteoriteName);
                views.setTextViewText(R.id.year, year);
                setLocationText(addressService.getAddressFromId(idString), views);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, meteoriteName);
                }

                final Intent intent = new Intent(getBaseContext(), MeteoriteDetailActivity.class);
                intent.putExtra(ITEM_SELECTED, idString);

                views.setOnClickFillInIntent(R.id.widget_list_item, intent);

                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_list_item, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.meteorite_detail_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (cursor.moveToPosition(position))
                    return cursor.getLong(ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
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

}
