package com.antonio.samir.meteoritelandingsspots.ui.recyclerView;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;
import com.antonio.samir.meteoritelandingsspots.presenter.MeteoriteListPresenter;
import com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector.MeteoriteSelector;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
public class MeteoriteAdapter extends RecyclerView.Adapter<ViewHolderMeteorite> {
    private final MeteoriteSelector meteoriteSelector;
    private final MeteoriteListPresenter mPresenter;
    private Context mContext;
    private String mSelectedMeteorite;
    private ViewHolderMeteorite mViewHolderMeteorite;
    private List<Meteorite> mMeteorites;

    public MeteoriteAdapter(Context context, final MeteoriteSelector meteoriteSelector, MeteoriteListPresenter presenter) {
        this.mContext = context;
        this.meteoriteSelector = meteoriteSelector;
        mPresenter = presenter;
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
        final int itemCount = (mMeteorites != null)? mMeteorites.size():0;
        return itemCount;
    }

    public void setData(final List<Meteorite> data) {
        mMeteorites = data;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(ViewHolderMeteorite viewHolder, int position) {
        final Meteorite meteorite = mMeteorites.get(position);

        final String meteoriteName = meteorite.getName();
        final String year = meteorite.getYearString();

        final String idString = String.valueOf(meteorite.getId());

        viewHolder.mName.setText(meteoriteName);
        viewHolder.mYear.setText(year);

        viewHolder.mName.setContentDescription(meteoriteName);
        viewHolder.mYear.setContentDescription(year);

        setLocationText(meteorite, viewHolder);

        viewHolder.setId(idString);


        int color = R.color.unselected_item_color;
        int title_color = R.color.title_color;
        int elevation = R.dimen.unselected_item_elevation;

        if (StringUtils.equals(idString, mSelectedMeteorite)) {
            color = R.color.selected_item_color;
            title_color = R.color.selected_title_color;
            elevation = R.dimen.selected_item_elevation;
        }

        viewHolder.mCardview.setCardBackgroundColor(mContext.getResources().getColor(color));
        viewHolder.mCardview.setCardElevation(mContext.getResources().getDimensionPixelSize(elevation));
        viewHolder.mName.setTextColor(mContext.getResources().getColor(title_color));

    }


    public void setLocationText(final Meteorite meteorite, final ViewHolderMeteorite viewHolder) {
        final String address = meteorite.getAddress();

        if (viewHolder.liveMet != null && viewHolder.addressObserver != null) {
            viewHolder.liveMet.removeObserver(viewHolder.addressObserver);
        }

        if (StringUtils.isNotEmpty(address)) {
            showAddress(viewHolder, address);
        } else {
            viewHolder.liveMet = mPresenter.getMeteorite(meteorite);
            viewHolder.addressObserver = meteorite1 -> {
                final String newAddress = meteorite1.getAddress();
                if (StringUtils.isNotEmpty(newAddress)) {
                    showAddress(viewHolder, newAddress);
                    viewHolder.liveMet.removeObserver(viewHolder.addressObserver);
                }
            };
            viewHolder.liveMet.observeForever(viewHolder.addressObserver);
            viewHolder.mLocation.setVisibility(View.GONE);
        }
    }

    private void showAddress(ViewHolderMeteorite viewHolder, String address) {
        viewHolder.mLocation.setText(address);
        viewHolder.mLocation.setVisibility(View.VISIBLE);

    }

    public void setSelectedMeteorite(final String selectedMeteorite) {
        notifyDataSetChanged();
        this.mSelectedMeteorite = selectedMeteorite;
    }

    public ViewHolderMeteorite getmVieHolderMeteorite() {
        return mViewHolderMeteorite;
    }
}