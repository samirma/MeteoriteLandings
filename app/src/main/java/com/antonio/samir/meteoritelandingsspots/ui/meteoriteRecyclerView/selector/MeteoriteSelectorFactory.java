package com.antonio.samir.meteoritelandingsspots.ui.meteoriteRecyclerView.selector;

/**
 * MeteoriteSelector factory used by MeteoriteAdapter to prevent it figure out
 * which should be the proper command to execute
 */
public class MeteoriteSelectorFactory {


    public static MeteoriteSelector getMeteoriteSelector(boolean isLandscape, final MeteoriteSelectorView view) {
        final MeteoriteSelector meteoriteSelector;
        if (isLandscape) {
            meteoriteSelector = new MeteoriteSelectorLandscape(view);
        } else {
            meteoriteSelector = new MeteoriteSelectorPortrait(view);
        }
        return meteoriteSelector;
    }
}
