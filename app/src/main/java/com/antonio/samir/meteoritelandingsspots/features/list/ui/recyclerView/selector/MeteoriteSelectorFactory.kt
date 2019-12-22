package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector

/**
 * MeteoriteSelector factory used by MeteoriteAdapter to prevent it figure out
 * which should be the proper command to execute
 */
object MeteoriteSelectorFactory {


    fun getMeteoriteSelector(isLandscape: Boolean, view: MeteoriteSelectorView): MeteoriteSelector {
        val meteoriteSelector: MeteoriteSelector
        if (isLandscape) {
            meteoriteSelector = MeteoriteSelectorLandscape(view)
        } else {
            meteoriteSelector = MeteoriteSelectorPortrait(view)
        }
        return meteoriteSelector
    }
}
