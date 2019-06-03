package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector

/**
 * Interface used by view activity to implement proper commands for each meteorite selection case
 */
interface MeteoriteSelectorView {

//    /**
//     * Exceute landscape command for meteorite selection
//     * @param meteorite
//     */
//    fun selectLandscape(meteorite: String?)

    /**
     * Exceute portrait command for meteorite selection
     * @param meteorite
     */
    fun selectPortrait(meteorite: String?)

}
