package com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector;

import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

/**
 * Interface used by view activity to implement proper commands for each meteorite selection case
 */
public interface MeteoriteSelectorView {

    /**
     * Exceute landscape command for meteorite selection
     * @param meteorite
     */
    void selectLandscape(final Meteorite meteorite);

    /**
     * Exceute portrait command for meteorite selection
     * @param meteorite
     */
    void selectPortrait(final Meteorite meteorite);

}
