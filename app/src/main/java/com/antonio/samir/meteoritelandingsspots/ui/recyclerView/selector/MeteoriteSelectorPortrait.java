package com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector;


public class MeteoriteSelectorPortrait implements MeteoriteSelector {

    private final MeteoriteSelectorView view;

    public MeteoriteSelectorPortrait(MeteoriteSelectorView view) {
        this.view = view;
    }

    @Override
    public void selectItemId(final String meteorite) {
        view.selectPortrait(meteorite);
    }
}
