package com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector


class MeteoriteSelectorLandscape(private val view: MeteoriteSelectorView) : MeteoriteSelector {

    override fun selectItemId(meteorite: String) {
        view.selectLandscape(meteorite)
    }

}
