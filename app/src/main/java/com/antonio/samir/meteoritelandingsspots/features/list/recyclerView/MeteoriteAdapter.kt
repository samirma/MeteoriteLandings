package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedListAdapter
import androidx.paging.PagingDataAdapter
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import java.util.*

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
class MeteoriteAdapter : PagingDataAdapter<Meteorite, ViewHolderMeteorite>(MeteoriteDiffCallback()) {

    var location: Location? = null

    var openMeteorite = MutableLiveData<Meteorite>()

    private var selectedMeteorite: Meteorite? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMeteorite {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_meteorite, parent, false)

        return ViewHolderMeteorite(view)
    }

    fun updateListUI(current: Meteorite?, previous: Meteorite? = selectedMeteorite) {
        if (!Objects.equals(previous, current)) {
            getPosition(current)?.let { notifyItemChanged(it) }
            getPosition(previous)?.let { notifyItemChanged(it) }
        }
        selectedMeteorite = current
    }

    fun getPosition(current: Meteorite?): Int? {
        return currentList?.indexOf(current)
    }

    override fun onBindViewHolder(viewHolder: ViewHolderMeteorite, position: Int) {
        getItem(position)?.let { meteorite ->
            val isSelected = selectedMeteorite == meteorite
            viewHolder.onBind(meteorite, isSelected, location) {
                openMeteorite.value = meteorite
            }
        }
    }

    fun clearSelectedMeteorite() {
        openMeteorite = MutableLiveData<Meteorite>()
    }

}