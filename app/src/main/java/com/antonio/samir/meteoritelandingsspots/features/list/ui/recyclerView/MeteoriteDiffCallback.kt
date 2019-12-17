package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

class MeteoriteDiffCallback() : DiffUtil.ItemCallback<Meteorite>() {

    override fun areItemsTheSame(oldMeteorites: Meteorite, newMeteorites: Meteorite): Boolean {
        return oldMeteorites.id == newMeteorites.id
    }

    override fun areContentsTheSame(oldItemPosition: Meteorite, newItemPosition: Meteorite): Boolean {
        return oldItemPosition.address == newItemPosition.address
    }

}