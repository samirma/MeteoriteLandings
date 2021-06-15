package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingDataAdapter
import com.antonio.samir.meteoritelandingsspots.databinding.ListItemMeteoriteBinding
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
class MeteoriteAdapter : PagingDataAdapter<MeteoriteItemView, ViewHolderMeteorite>(MeteoriteDiffCallback()) {

    var location: Location? = null

    var openMeteorite = MutableLiveData<MeteoriteItemView>()

    private var selectedMeteorite: MeteoriteItemView? = null

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

    fun updateListUI(current: MeteoriteItemView?) {
        notifyItemChanged(selectedPosition)
        notifyItemChanged(previousPosition)
        selectedMeteorite = current
    }


    override fun onBindViewHolder(viewHolder: ViewHolderMeteorite, position: Int) {
        getItem(position)?.let { meteorite ->
            val isSelected = selectedMeteorite == meteorite
            viewHolder.onBind(meteorite, isSelected) {
                openMeteorite.value = meteorite
                previousPosition = selectedPosition
                selectedPosition = viewHolder.absoluteAdapterPosition
            }
        }
    }

    fun clearSelectedMeteorite() {
        openMeteorite = MutableLiveData<MeteoriteItemView>()
    }

    fun getSelectedPosition() = selectedPosition

}