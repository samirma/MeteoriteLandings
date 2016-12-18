package com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector;


import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

/**
 * Meteorite Selector interface to abstract the selection command from Meteorite Adapter
 */
public interface MeteoriteSelector {

    void selectItemId(final String meteorite);

}
