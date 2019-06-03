package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector


/**
 * Meteorite Selector interface to abstract the selection command from Meteorite Adapter
 */
interface MeteoriteSelector {

    fun selectItemId(meteorite: String?)

}
