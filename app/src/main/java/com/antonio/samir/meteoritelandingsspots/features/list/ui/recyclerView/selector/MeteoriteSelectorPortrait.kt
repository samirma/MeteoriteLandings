package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite


class MeteoriteSelectorPortrait(private val view: MeteoriteSelectorView) : MeteoriteSelector {

    override fun selectItem(meteorite: Meteorite) {
        view.selectPortrait(meteorite)
    }
}
