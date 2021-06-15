package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.databinding.ListItemMeteoriteBinding

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
class MeteoriteAdapter : PagingDataAdapter<Meteorite, ViewHolderMeteorite>(MeteoriteDiffCallback()) {

    var location: Location? = null

    var openMeteorite = MutableLiveData<Meteorite>()

    private var selectedMeteorite: Meteorite? = null

    private var selectedPosition = -1
    private var previousPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMeteorite {
        val itemBinding = ListItemMeteoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolderMeteorite(itemBinding)
    }

    fun updateListUI(current: Meteorite?) {
        notifyItemChanged(selectedPosition)
        notifyItemChanged(previousPosition)
        selectedMeteorite = current
    }


    override fun onBindViewHolder(viewHolder: ViewHolderMeteorite, position: Int) {
        getItem(position)?.let { meteorite ->
            val isSelected = selectedMeteorite == meteorite
            viewHolder.onBind(meteorite, isSelected, location) {
                openMeteorite.value = meteorite
                previousPosition = selectedPosition
                selectedPosition = viewHolder.absoluteAdapterPosition
            }
        }
    }

    fun clearSelectedMeteorite() {
        openMeteorite = MutableLiveData<Meteorite>()
    }

    fun getSelectedPosition() = selectedPosition

}