package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector

/**
 * Interface used by view activity to implement proper commands for each meteorite selection case
 */
interface MeteoriteSelectorView {

    /**
     * Exceute landscape command for meteorite selection
     * @param meteoriteId
     */
    fun selectLandscape(meteoriteId: String)

    /**
     * Exceute portrait command for meteorite selection
     * @param meteoriteId
     */
    fun selectPortrait(meteoriteId: String?)

}
