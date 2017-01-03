package com.antonio.samir.meteoritelandingsspots.presenter;


import android.content.Context;
import android.database.Cursor;

public interface MeteoriteListView {
    Context getContext();

    void onPreExecute();

    void setMeteorites(Cursor result);

    void unableToFetch();

    void error(String s);

    void clearList();

    void showList();

    void hideList();

}
