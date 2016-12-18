package com.antonio.samir.meteoritelandingsspots.ui.recyclerView.selector;


public class MeteoriteSelectorLandscape implements MeteoriteSelector {

    private final MeteoriteSelectorView view;

    public MeteoriteSelectorLandscape(MeteoriteSelectorView view) {
        this.view = view;
    }

    @Override
    public void selectItemId(final String meteorite) {
        view.selectLandscape(meteorite);
    }

}
