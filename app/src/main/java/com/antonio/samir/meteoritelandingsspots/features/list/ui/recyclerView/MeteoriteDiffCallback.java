package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView;

import androidx.recyclerview.widget.DiffUtil;

import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite;

import java.util.List;
import java.util.Objects;

public class MeteoriteDiffCallback extends DiffUtil.Callback {

    private List<Meteorite> oldMeteorites;
    private List<Meteorite> newMeteorites;

    public MeteoriteDiffCallback(List<Meteorite> newMeteorites, List<Meteorite> oldMeteorites) {
        this.newMeteorites = newMeteorites;
        this.oldMeteorites = oldMeteorites;
    }

    @Override
    public int getOldListSize() {
        return oldMeteorites.size();
    }

    @Override
    public int getNewListSize() {
        return newMeteorites.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldMeteorites.get(oldItemPosition).getId() == newMeteorites.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Meteorite oldMeteorite = oldMeteorites.get(oldItemPosition);
        final Meteorite newMeteorite = newMeteorites.get(newItemPosition);
        return Objects.equals(oldMeteorite.getAddress(), newMeteorite.getAddress());
    }

    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}