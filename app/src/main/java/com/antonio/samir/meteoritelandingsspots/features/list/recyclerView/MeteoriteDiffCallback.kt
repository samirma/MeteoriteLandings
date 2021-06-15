package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView

class MeteoriteDiffCallback : DiffUtil.ItemCallback<MeteoriteItemView>() {

    override fun areItemsTheSame(oldMeteorites: MeteoriteItemView, newMeteorites: MeteoriteItemView): Boolean {
        return oldMeteorites.id == newMeteorites.id
    }

    override fun areContentsTheSame(oldItemPosition: MeteoriteItemView, newItemPosition: MeteoriteItemView): Boolean {
        return oldItemPosition.address == newItemPosition.address
    }

}