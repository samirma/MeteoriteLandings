package com.antonio.samir.meteoritelandingsspots.ui.meteoriteRecyclerView.selector;


import com.antonio.samir.meteoritelandingsspots.model.Meteorite;

public class MeteoriteSelectorLandscape implements MeteoriteSelector {

    private final MeteoriteSelectorView view;

    public MeteoriteSelectorLandscape(MeteoriteSelectorView view) {
        this.view = view;
    }

    @Override
    public void selectItemId(final Meteorite meteorite) {
        view.selectLandscape(meteorite);
    }

}
