package com.antonio.samir.meteoritelandingsspots.features.list.mapper

import android.content.Context
import android.location.Location
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.mapper.MapperBase
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.features.getLocationText
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.yearString

class MeteoriteViewMapper (val context: Context): MapperBase<MeteoriteViewMapper.Input, MeteoriteItemView>(){

    override suspend fun action(input: Input): MeteoriteItemView {

        val meteorite = input.meteorite

        return MeteoriteItemView(
            id = meteorite.id,
            name = meteorite.name,
            yearString = meteorite.yearString,
            address = meteorite.getLocationText(
                location = input.location,
                noAddress = context
                    .getString(R.string.without_address_placeholder)
            ),
            reclat = meteorite.reclat,
            reclong = meteorite.reclong
        )
    }

    data class Input(val meteorite: Meteorite, val location: Location?)

}