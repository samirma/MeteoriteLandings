package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import android.location.Location
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import java.util.*

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
class MeteoriteAdapter : PagedListAdapter<Meteorite, ViewHolderMeteorite>(MeteoriteDiffCallback()) {

    var location: Location? = null

    var selectedMeteorite = MutableLiveData<Meteorite>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMeteorite {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_meteorite, parent, false)
        val vh = ViewHolderMeteorite(view)

        //On view click use MeteoriteSelector to do execute the proper according the current layout
        view.setOnClickListener {
            val previous = selectedMeteorite.value
            val current = vh.meteorite
            selectedMeteorite.value = current
            updateListUI(current, previous)
        }
        return vh
    }

    private fun updateListUI(current: Meteorite?, previous: Meteorite?) {
        if (!Objects.equals(previous, current)) {
            currentList?.indexOf(current)?.let { notifyItemChanged(it) }
            currentList?.indexOf(previous)?.let { notifyItemChanged(it) }
        }
    }

    fun setData(meteorites: PagedList<Meteorite>) {
        submitList(meteorites)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(viewHolder: ViewHolderMeteorite, position: Int) {
        getItem(position)?.let { meteorite ->
            val isSelected = selectedMeteorite.value == meteorite
            viewHolder.onBind(meteorite, isSelected, location)
        }
    }

    override fun getItemId(position: Int): Long {
        val item = getItem(position)
        return if (item != null) {
            (item.id.hashCode() + item.name.hashCode()).toLong()
        } else {
            super.getItemId(position)
        }
    }

}