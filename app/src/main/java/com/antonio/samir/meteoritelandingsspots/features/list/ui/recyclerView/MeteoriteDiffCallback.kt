package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite

class MeteoriteDiffCallback(
        private val newMeteorites: List<Meteorite>,
        private val oldMeteorites: List<Meteorite>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldMeteorites.size
    }

    override fun getNewListSize(): Int {
        return newMeteorites.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMeteorites[oldItemPosition].id == newMeteorites[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldMeteorite = oldMeteorites[oldItemPosition]
        val newMeteorite = newMeteorites[newItemPosition]
        return oldMeteorite.address == newMeteorite.address
    }
    
}