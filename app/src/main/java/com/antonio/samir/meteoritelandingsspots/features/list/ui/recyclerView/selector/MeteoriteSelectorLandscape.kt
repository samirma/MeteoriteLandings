package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector


class MeteoriteSelectorLandscape(private val view: MeteoriteSelectorView) : MeteoriteSelector {

    override fun selectItemId(meteorite: String) {
        view.selectLandscape(meteorite)
    }

}
