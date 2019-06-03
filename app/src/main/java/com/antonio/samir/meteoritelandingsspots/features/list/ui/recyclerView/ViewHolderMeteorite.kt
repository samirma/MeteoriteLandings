package com.antonio.samir.meteoritelandingsspots.features.list.ui.recyclerView

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.model.Meteorite


class ViewHolderMeteorite(view: View) : RecyclerView.ViewHolder(view) {

    var mName: TextView

    var mLocation: TextView

    var mCardview: CardView

    var mYear: TextView
    var addressObserver: Observer<Meteorite>? = null
    var liveMet: LiveData<Meteorite>? = null
    var id: String? = null

    init {

        mName = view.findViewById(R.id.title)

        mLocation = view.findViewById(R.id.location)

        mCardview = view.findViewById(R.id.cardview)

        mYear = view.findViewById(R.id.year)

    }


}