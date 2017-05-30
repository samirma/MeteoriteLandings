package com.antonio.samir.meteoritelandingsspots.ui.recyclerView;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteProvider;
import com.antonio.samir.meteoritelandingsspots.service.server.AddressService;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector;

import org.apache.commons.lang3.StringUtils;

import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.ID;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAME;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECLAT;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.RECLONG;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.YEAR;

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
public class MeteoriteAdapter extends CursorRecyclerViewAdapter<ViewHolderMeteorite> {
    private final MeteoriteSelector meteoriteSelector;
    private Context mContext;
    private String mSelectedMeteorite;
    private ViewHolderMeteorite mViewHolderMeteorite;

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
                mViewHolderMeteorite = vh;
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

        final String reclat = cursor.getString(cursor.getColumnIndex(RECLAT));
        final String reclong = cursor.getString(cursor.getColumnIndex(RECLONG));


        final int id = cursor.getColumnIndex(ID);
        final String idString = cursor.getString(id);

        viewHolder.name.setText(meteoriteName);
        viewHolder.year.setText(year);

        viewHolder.name.setContentDescription(meteoriteName);
        viewHolder.year.setContentDescription(year);

        recoverAddress(viewHolder, idString, reclat, reclong);

        viewHolder.setId(idString);


        int color = R.color.unselected_item_color;
        int title_color = R.color.title_color;
        int elevation = R.dimen.unselected_item_elevation;

        if (StringUtils.equals(idString, mSelectedMeteorite)) {
            color = R.color.selected_item_color;
            title_color = R.color.selected_title_color;
            elevation = R.dimen.selected_item_elevation;
        }

        viewHolder.cardView.setCardBackgroundColor(mContext.getResources().getColor(color));
        viewHolder.cardView.setCardElevation(mContext.getResources().getDimensionPixelSize(elevation));
        viewHolder.name.setTextColor(mContext.getResources().getColor(title_color));

    }



    private void recoverAddress(final ViewHolderMeteorite viewHolder, final String idString, String reclat, String reclong) {

        final ContentResolver contentResolver = mContext.getContentResolver();

        final AddressService addressService = new AddressService(contentResolver);

        String address = addressService.getAddressFromId(idString);

        if (StringUtils.isEmpty(address)) {
            final Uri uri = MeteoriteProvider.Addresses.withId(idString);
            viewHolder.observer = new ContentObserver(new Handler()) {
                @Override
                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    final String address = addressService.getAddressFromId(idString);
                    setLocationText(address, viewHolder);
                    unregisterObserver(viewHolder, contentResolver);
                }
            };
            contentResolver.registerContentObserver(uri, true, viewHolder.observer);
        } else {
            unregisterObserver(viewHolder, contentResolver);
        }

        setLocationText(address, viewHolder);
        viewHolder.location.setContentDescription(address);


    }

    private void unregisterObserver(ViewHolderMeteorite viewHolder, ContentResolver contentResolver) {
        if (viewHolder.observer != null) {
            contentResolver.unregisterContentObserver(viewHolder.observer);
            viewHolder.observer = null;
        }
    }

    public void setLocationText(final String address, final ViewHolderMeteorite viewHolder) {
        final int visibility;
        if (StringUtils.isNotEmpty(address)) {
            viewHolder.location.setText(address);
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        viewHolder.location.setVisibility(visibility);
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolderMeteorite holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder.observer != null) {
            mContext.getContentResolver().unregisterContentObserver(holder.observer);
        }
    }


    public void setSelectedMeteorite(final String selectedMeteorite) {
        notifyDataSetChanged();
        this.mSelectedMeteorite = selectedMeteorite;
    }

    public ViewHolderMeteorite getmViewHolderMeteorite() {
        return mViewHolderMeteorite;
    }
}