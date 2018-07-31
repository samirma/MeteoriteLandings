package com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector


class MeteoriteSelectorPortrait(private val view: MeteoriteSelectorView) : MeteoriteSelector {

    override fun selectItemId(meteorite: String?) {
        view.selectPortrait(meteorite)
    }
}
