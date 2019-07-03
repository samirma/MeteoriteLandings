package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

/**
 * Interface used by view activity to implement proper commands for each meteorite selection case
 */
interface MeteoriteSelectorView {

    /**
     * Exceute landscape command for meteorite selection
     * @param meteoriteId
     */
    fun selectLandscape(meteoriteId: Meteorite)

    /**
     * Exceute portrait command for meteorite selection
     * @param meteoriteId
     */
    fun selectPortrait(meteorite: Meteorite)

}
