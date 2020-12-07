package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import android.content.Context
import android.location.Location
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.finalAddress
import com.antonio.samir.meteoritelandingsspots.features.yearString
import org.apache.commons.lang3.StringUtils

class ViewHolderMeteorite(view: View) : RecyclerView.ViewHolder(view) {

    val context: Context = view.context

    var name: TextView = view.findViewById(R.id.title)

    var addressTV: TextView = view.findViewById(R.id.location)

    var cardView: CardView = view.findViewById(R.id.cardview)

    var meteorite: Meteorite? = null

    fun onBind(
            meteorite: Meteorite,
            selectedMeteorite: Boolean,
            location: Location?
    ) {

        if (this.meteorite == meteorite) {
            setLocationText(meteorite, location)
        } else {
            this.meteorite = meteorite
            populateViewHolder(meteorite, location, selectedMeteorite)
        }

    }

    private fun populateViewHolder(meteorite: Meteorite, location: Location?, selectedMeteorite: Boolean) {
        val meteoriteName = meteorite.name

        val year = meteorite.yearString

        name.text = context.getString(R.string.title, meteoriteName, year)

        name.contentDescription = meteoriteName

        setLocationText(meteorite, location)

        var color = R.color.unselected_item_color
        var titleColor = R.color.title_color
        var elevation = R.dimen.unselected_item_elevation

        if (selectedMeteorite) {
            color = R.color.selected_item_color
            titleColor = R.color.selected_title_color
            elevation = R.dimen.selected_item_elevation
        }

        cardView.setCardBackgroundColor(context.resources.getColor(color))
        cardView.cardElevation = context.resources.getDimensionPixelSize(elevation).toFloat()
        name.setTextColor(context.resources.getColor(titleColor))
    }


    private fun setLocationText(meteorite: Meteorite, location: Location?) {
        val address = meteorite.finalAddress(location)

        if (!address.isNullOrEmpty()) {
            addressTV.text = address
        } else {
            addressTV.text = context.getString(R.string.without_address_placeholder)
        }
    }

}