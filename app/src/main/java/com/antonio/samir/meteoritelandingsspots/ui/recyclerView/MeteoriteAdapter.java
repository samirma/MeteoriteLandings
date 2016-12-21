package com.antonio.samir.meteoritelandingsspots.ui.recyclerView;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector;

import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.ID;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.NAME;
import static com.antonio.samir.meteoritelandingsspots.service.repository.MeteoriteColumns.YEAR;

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
public class MeteoriteAdapter extends CursorRecyclerViewAdapter<ViewHolderMeteorite> {
    private final MeteoriteSelector meteoriteSelector;
    private Context mContext;

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
        final String location = cursor.getString(cursor.getColumnIndex(NAME));
        final String year = cursor.getString(cursor.getColumnIndex(YEAR));


        viewHolder.name.setText(meteoriteName);
        viewHolder.location.setText("Some City");

        viewHolder.year.setText(year);


        final int id = cursor.getColumnIndex(ID);
        viewHolder.setId(cursor.getString(id));
    }

}