package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite

class MeteoriteDiffCallback : DiffUtil.ItemCallback<Meteorite>() {

    override fun areItemsTheSame(oldMeteorites: Meteorite, newMeteorites: Meteorite): Boolean {
        return oldMeteorites.id == newMeteorites.id
    }

    override fun areContentsTheSame(oldItemPosition: Meteorite, newItemPosition: Meteorite): Boolean {
        return oldItemPosition.address == newItemPosition.address
    }

}