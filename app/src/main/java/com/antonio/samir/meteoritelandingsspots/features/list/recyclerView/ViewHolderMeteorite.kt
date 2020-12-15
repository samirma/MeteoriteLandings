package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import android.content.Context
import android.location.Location
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.getLocationText
import com.antonio.samir.meteoritelandingsspots.features.yearString

class ViewHolderMeteorite(val view: View) : RecyclerView.ViewHolder(view) {

    val context: Context = view.context

    var name: TextView = view.findViewById(R.id.title)

    var addressTV: TextView = view.findViewById(R.id.location)

    var cardView: CardView = view.findViewById(R.id.cardview)

    var meteorite: Meteorite? = null

    fun onBind(
            meteorite: Meteorite,
            selectedMeteorite: Boolean,
            location: Location?,
            onClick: () -> Unit
    ) {

        if (this.meteorite?.id == meteorite.id) {
            addressTV.text = meteorite.getLocationText(context, location)
        } else {
            this.meteorite = meteorite
            populateViewHolder(meteorite, location, selectedMeteorite)
        }

        view.setOnClickListener {
            onClick()
        }

    }

    private fun populateViewHolder(meteorite: Meteorite, location: Location?, selectedMeteorite: Boolean) {
        val meteoriteName = meteorite.name

        val year = meteorite.yearString

        name.text = if (!year.isNullOrBlank()) {
            context.getString(R.string.name, meteoriteName, year)
        } else {
            meteoriteName
        }

        name.contentDescription = meteoriteName

        addressTV.text = meteorite.getLocationText(context, location)

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


}