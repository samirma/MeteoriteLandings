package com.antonio.samir.meteoritelandingsspots.ui.recyclerView;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderMeteorite extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    public TextView mName;

    @BindView(R.id.location)
    public TextView mLocation;

    @BindView(R.id.cardview)
    public CardView mCardview;

    @BindView(R.id.year)
    public TextView mYear;

    private String mId;

    public Observer<Meteorite> addressObserver;
    public LiveData<Meteorite> liveMet;

    public ViewHolderMeteorite(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }


}