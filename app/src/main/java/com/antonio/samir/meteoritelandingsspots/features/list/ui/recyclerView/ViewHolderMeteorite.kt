package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite


class ViewHolderMeteorite(view: View) : RecyclerView.ViewHolder(view) {

    var name: TextView

    var location: TextView

    var mCardview: CardView

    var addressObserver: Observer<Meteorite>? = null
    var liveMet: LiveData<Meteorite>? = null
    var id: String? = null

    init {

        name = view.findViewById(R.id.title)

        location = view.findViewById(R.id.location)

        mCardview = view.findViewById(R.id.cardview)


    }


}