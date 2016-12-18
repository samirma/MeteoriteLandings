package com.antonio.samir.meteoritelandingsspots.ui.recyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderMeteorite extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    public TextView titleView;

    public ViewHolderMeteorite(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}