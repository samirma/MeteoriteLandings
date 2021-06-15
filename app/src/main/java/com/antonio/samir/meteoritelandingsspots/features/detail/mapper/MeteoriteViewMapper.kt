package com.antonio.samir.meteoritelandingsspots.features.detail.mapper

import android.content.Context
import android.location.Location
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.mapper.MapperBase
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.detail.MeteoriteView
import com.antonio.samir.meteoritelandingsspots.features.getLocationText
import com.antonio.samir.meteoritelandingsspots.features.yearString
import com.antonio.samir.meteoritelandingsspots.ui.extension.convertToNumberFormat

class MeteoriteMapper : MapperBase<MeteoriteMapper.Input, MeteoriteView>() {

    override suspend fun action(input: Input): MeteoriteView = MeteoriteView(
        id = input.meteorite.id.toString(),
        name = input.meteorite.name,
        yearString = input.meteorite.yearString,
        address = input.meteorite.getLocationText(
            location = input.location,
            noAddress = input.context
                .getString(R.string.without_address_placeholder)
        ),
        recclass = input.meteorite.recclass,
        mass = input.meteorite.mass.convertToNumberFormat(input.context.getString(R.string.unkown)),
        reclat = input.meteorite.reclat?.toDouble() ?: 0.0,
        reclong = input.meteorite.reclong?.toDouble() ?: 0.0,
        hasAddress = !input.meteorite.address.isNullOrBlank()
    )

    data class Input(val meteorite: Meteorite, val location: Location?, val context: Context)

}