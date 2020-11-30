package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView.selector

import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite


/**
 * Meteorite Selector interface to abstract the selection command from Meteorite Adapter
 */
interface MeteoriteSelector {

    fun selectItem(meteorite: Meteorite)

}
