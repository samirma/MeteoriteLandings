package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite


/**
 * Meteorite Selector interface to abstract the selection command from Meteorite Adapter
 */
interface MeteoriteSelector {

    fun selectItem(meteorite: Meteorite)

}
