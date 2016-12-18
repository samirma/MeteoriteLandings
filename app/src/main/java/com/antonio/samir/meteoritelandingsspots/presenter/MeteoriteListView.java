package com.antonio.samir.meteoritelandingsspots.presenter;


import android.content.Context;
import android.database.Cursor;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

import java.util.List;

public interface MeteoriteListView {
    Context getContext();

    void onPreExecute();

    void setMeteorites(Cursor result);

    void errorFeitch();

    void unableToFetch();

    void error(String s);

    void clearList();

    void showDatedMessage();

    void hideDatedMessage();

    void showList();

    void hideList();

}
