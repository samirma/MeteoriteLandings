package com.antonio.samir.meteoritelandingsspots.features.list.recyclerView

import androidx.recyclerview.widget.RecyclerView
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.databinding.ListItemMeteoriteBinding
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView

class ViewHolderMeteorite(private val binding: ListItemMeteoriteBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val context = itemView.context

    var meteorite: MeteoriteItemView? = null

    fun onBind(
        meteorite: MeteoriteItemView,
        selectedMeteorite: Boolean,
        onClick: () -> Unit
    ) {

        if (this.meteorite?.id == meteorite.id) {
            binding.info.location.text = meteorite.address
        } else {
            this.meteorite = meteorite
            populateViewHolder(meteorite, selectedMeteorite)
        }

        itemView.setOnClickListener {
            onClick()
        }

    }

    private fun populateViewHolder(
        meteorite: MeteoriteItemView,
        selectedMeteorite: Boolean
    ) {
        val meteoriteName = meteorite.name

        val year = meteorite.yearString

        var color = R.color.unselected_item_color
        var titleColor = R.color.title_color
        var elevation = R.dimen.unselected_item_elevation
        if (selectedMeteorite) {
            color = R.color.selected_item_color
            titleColor = R.color.selected_title_color
            elevation = R.dimen.selected_item_elevation
        }

        with(binding) {

            with(info) {
                title.text = if (!year.isNullOrBlank()) {
                    context.getString(R.string.name, meteoriteName, year)
                } else {
                    meteoriteName
                }

                title.contentDescription = meteoriteName


                title.setTextColor(context.resources.getColor(titleColor))
            }

            info.location.text = meteorite.address

            cardview.setCardBackgroundColor(context.resources.getColor(color))
            cardview.cardElevation = context.resources.getDimensionPixelSize(elevation).toFloat()
        }
    }


}