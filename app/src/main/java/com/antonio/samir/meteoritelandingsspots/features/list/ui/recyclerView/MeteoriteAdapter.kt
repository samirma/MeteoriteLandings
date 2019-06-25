package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView.selector.MeteoriteSelector
import com.antonio.samir.meteoritelandingsspots.features.list.viewmodel.MeteoriteListViewModel
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import org.apache.commons.lang3.StringUtils

/**
 * Custom RecyclerView.Adapter to deal with meteorites cursor
 */
class MeteoriteAdapter(
        private val context: Context,
        private val meteoriteSelector: MeteoriteSelector,
        private val viewModel: MeteoriteListViewModel
) : androidx.recyclerview.widget.RecyclerView.Adapter<ViewHolderMeteorite>() {

    private var selectedMeteorite: String? = null

    var vieHolderMeteorite: ViewHolderMeteorite? = null
        private set

    private var meteorites: List<Meteorite>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderMeteorite {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_meteorite, parent, false)
        val vh = ViewHolderMeteorite(view)

        //On view click use MeteoriteSelector to do execute the proper according the current layout
        view.setOnClickListener { view1 ->
            vieHolderMeteorite = vh
            meteoriteSelector.selectItemId(vh.id)
        }
        return vh
    }


    override fun getItemCount(): Int {
        return if (meteorites != null) meteorites!!.size else 0
    }

    fun setData(meteorites: List<Meteorite>) {
        this.meteorites = meteorites
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onBindViewHolder(viewHolder: ViewHolderMeteorite, position: Int) {
        val meteorite = meteorites!![position]

        val meteoriteName = meteorite.name
        val year = meteorite.yearString

        val idString = meteorite.id.toString()

        viewHolder.name.text = context.getString(R.string.title, meteoriteName, year)

        viewHolder.name.contentDescription = meteoriteName

        setLocationText(meteorite, viewHolder)

        viewHolder.id = idString


        var color = R.color.unselected_item_color
        var title_color = R.color.title_color
        var elevation = R.dimen.unselected_item_elevation

        if (StringUtils.equals(idString, selectedMeteorite)) {
            color = R.color.selected_item_color
            title_color = R.color.selected_title_color
            elevation = R.dimen.selected_item_elevation
        }

        viewHolder.mCardview.setCardBackgroundColor(context.resources.getColor(color))
        viewHolder.mCardview.cardElevation = context.resources.getDimensionPixelSize(elevation).toFloat()
        viewHolder.name.setTextColor(context.resources.getColor(title_color))

    }


    private fun setLocationText(meteorite: Meteorite, viewHolder: ViewHolderMeteorite) {
        val address = meteorite.address

        //Always remove the previous observer
        if (viewHolder.liveMet != null && viewHolder.addressObserver != null) {
            viewHolder.liveMet?.removeObserver(viewHolder.addressObserver!!)
            viewHolder.liveMet = null
            viewHolder.addressObserver = null
        }

        if (StringUtils.isNotEmpty(address)) {
            showAddress(viewHolder, address, meteorite)
        } else {
            //If address is still empty then observe this entity to be aware of any change
            viewHolder.liveMet = viewModel.getMeteorite(meteorite)

            val addressObserver = Observer<Meteorite> { meteorite1 ->
                val newAddress = meteorite1?.address
                if (StringUtils.isNotEmpty(newAddress)) {
                    showAddress(viewHolder, newAddress!!, meteorite)
                    viewHolder.location.startAnimation(AnimationUtils.loadAnimation(context, R.anim.view_show))
                    viewHolder.liveMet?.removeObserver(viewHolder.addressObserver!!)
                }
            }

            viewHolder.addressObserver = addressObserver
            viewHolder.liveMet?.observeForever(viewHolder.addressObserver!!)
            viewHolder.location.visibility = View.INVISIBLE
        }
    }

    private fun showAddress(viewHolder: ViewHolderMeteorite, address: String?, meteorite: Meteorite) {
        viewHolder.location.text = address
        viewHolder.location.visibility = View.VISIBLE
    }

    fun setSelectedMeteorite(selectedMeteorite: String) {
        notifyDataSetChanged()
        this.selectedMeteorite = selectedMeteorite
    }
}