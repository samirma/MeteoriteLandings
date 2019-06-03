package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector


class MeteoriteSelectorPortrait(private val view: MeteoriteSelectorView) : MeteoriteSelector {

    override fun selectItemId(meteorite: String?) {
        view.selectPortrait(meteorite)
    }
}
