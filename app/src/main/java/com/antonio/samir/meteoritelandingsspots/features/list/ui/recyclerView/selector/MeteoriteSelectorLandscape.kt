package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector

import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite


class MeteoriteSelectorLandscape(private val view: MeteoriteSelectorView) : MeteoriteSelector {

    override fun selectItem(meteorite: Meteorite) {
        view.selectLandscape(meteorite)
    }

}
