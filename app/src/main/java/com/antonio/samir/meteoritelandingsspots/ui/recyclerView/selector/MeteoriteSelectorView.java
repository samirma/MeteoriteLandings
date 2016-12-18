package com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector;

/**
 * Interface used by view activity to implement proper commands for each meteorite selection case
 */
public interface MeteoriteSelectorView {

    /**
     * Exceute landscape command for meteorite selection
     * @param meteorite
     */
    void selectLandscape(final String meteorite);

    /**
     * Exceute portrait command for meteorite selection
     * @param meteorite
     */
    void selectPortrait(final String meteorite);

}
