package com.antonio.samir.meteoritelandingsspots.ui.recyclerView;

import android.view.View;
import android.widget.TextView;

import com.antonio.samir.meteoritelandingsspots.R;
import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;


public class ViewHolderMeteorite extends RecyclerView.ViewHolder {

    public TextView mName;

    public TextView mLocation;

    public CardView mCardview;

    public TextView mYear;
    public Observer<Meteorite> addressObserver;
    public LiveData<Meteorite> liveMet;
    private String mId;

    public ViewHolderMeteorite(View view) {
        super(view);

        mName = view.findViewById(R.id.title);

        mLocation = view.findViewById(R.id.location);

        mCardview = view.findViewById(R.id.cardview);

        mYear = view.findViewById(R.id.year);

    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        this.mId = id;
    }


}