package com.antonio.samir.meteoritelandingsspots.ui.recyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector;

import org.apache.commons.lang3.StringUtils;

import static com.antonio.samir.meteoritelandingsspots.service.repository.AddressColumns.ADDRESS;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.ID;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAME;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.YEAR;

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
public class MeteoriteAdapter extends CursorRecyclerViewAdapter<ViewHolderMeteorite> {
    private final MeteoriteSelector meteoriteSelector;
    private Context mContext;
    private ContentObserver observer;

    public MeteoriteAdapter(Context context, Cursor cursor, final MeteoriteSelector meteoriteSelector) {
        super(context, cursor);
        this.mContext = context;
        this.meteoriteSelector = meteoriteSelector;
    }

    @Override
    public ViewHolderMeteorite onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_meteorite, parent, false);
        final ViewHolderMeteorite vh = new ViewHolderMeteorite(view);

        //On view click use MeteoriteSelector to do execute the proper according the current layout
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meteoriteSelector.selectItemId(vh.getId());
            }
        });
        return vh;
    }

    @Override
    public int getItemCount() {
        final int itemCount = super.getItemCount();
        return itemCount;
    }

    public void resetData() {
        swapCursor(null);
    }

    public void setData(Cursor data) {
        swapCursor(data);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(final ViewHolderMeteorite viewHolder, final Cursor cursor) {

        final String meteoriteName = cursor.getString(cursor.getColumnIndex(NAME));
        final String year = cursor.getString(cursor.getColumnIndex(YEAR));

        final int id = cursor.getColumnIndex(ID);
        final String idString = cursor.getString(id);

        viewHolder.name.setText(meteoriteName);

        viewHolder.year.setText(year);

        recoverAddress(viewHolder, idString);

        viewHolder.setId(idString);
    }

    private void recoverAddress(ViewHolderMeteorite viewHolder, String idString) {
        final ContentResolver contentResolver = mContext.getContentResolver();

        final Uri uri = MeteoriteProvider.Addresses.withId(idString);
        final Cursor cursor = contentResolver.query(uri,
                new String[]{ADDRESS},
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        String address = cursor.getString(cursor.getColumnIndex(ADDRESS));

        if (StringUtils.isBlank(address)) {
            address = "";
        }

        viewHolder.location.setText(address);

    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        if (observer != null) {
            mContext.getContentResolver().unregisterContentObserver(observer);
        }
    }

}