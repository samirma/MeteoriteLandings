package com.antonio.samir.meteoritelandingsspots.presenter;


import android.content.Context;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import java.util.List;

public interface MeteoriteListView {
    Context getContext();

    void onPreExecute();

    void setMeteorites(List<Meteorite> result);

    void unableToFetch();

    void error(String s);

    void clearList();

    void showList();

    void hideList();

}
