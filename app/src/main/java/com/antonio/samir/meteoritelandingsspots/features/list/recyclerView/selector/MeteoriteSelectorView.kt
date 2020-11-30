package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView.selector

import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite

/**
 * Interface used by view activity to implement proper commands for each meteorite selection case
 */
interface MeteoriteSelectorView {

    /**
     * Exceute landscape command for meteorite selection
     * @param meteorite
     */
    fun selectLandscape(meteorite: Meteorite)

    /**
     * Exceute portrait command for meteorite selection
     * @param meteorite
     */
    fun selectPortrait(meteorite: Meteorite)

}
