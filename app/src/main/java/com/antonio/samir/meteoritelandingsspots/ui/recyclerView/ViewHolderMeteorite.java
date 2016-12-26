package com.antonio.samir.meteoritelandingsspots.ui.recyclerView;

import android.database.ContentObserver;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ViewHolderMeteorite extends RecyclerView.ViewHolder {

    @BindView(R.id.title)
    public TextView name;

    @BindView(R.id.location)
    public TextView location;

    @BindView(R.id.year)
    public TextView year;
    public ContentObserver observer;
    private String id;

    public ViewHolderMeteorite(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}