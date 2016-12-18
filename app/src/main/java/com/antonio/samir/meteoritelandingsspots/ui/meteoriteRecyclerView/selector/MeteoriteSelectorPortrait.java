package com.antonio.samir.meteoritelandingsspots.ui.meteoriteRecyclerView.selector;


import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

public class MeteoriteSelectorPortrait implements MeteoriteSelector {

    private final MeteoriteSelectorView view;

    public MeteoriteSelectorPortrait(MeteoriteSelectorView view) {
        this.view = view;
    }

    @Override
    public void selectItemId(final Meteorite meteorite) {
        view.selectPortrait(meteorite);
    }
}
