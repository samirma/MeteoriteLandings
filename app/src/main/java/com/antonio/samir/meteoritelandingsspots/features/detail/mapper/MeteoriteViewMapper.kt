package com.antonio.samir.meteoritelandingsspots.features.detail.mapper

import android.content.Context
import android.location.Location
import com.antonio.samir.meteoritelandingsspots.R
import com.antonio.samir.meteoritelandingsspots.common.mapper.MapperBase
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.convertToNumberFormat
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.getLocationText
import com.antonio.samir.meteoritelandingsspots.common.ui.extension.yearString
import com.antonio.samir.meteoritelandingsspots.data.local.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteView
import javax.inject.Inject

class MeteoriteMapper @Inject constructor() : MapperBase<MeteoriteMapper.Input, MeteoriteView>() {

    override suspend fun action(input: Input): MeteoriteView = MeteoriteView(
        id = input.meteorite.id.toString(),
        name = input.meteorite.name ?: "",
        yearString = input.meteorite.yearString ?: "",
        address = input.meteorite.getLocationText(noAddress = ""),
        type = input.meteorite.recclass ?: "",
        mass = input.meteorite.mass.convertToNumberFormat(input.context.getString(R.string.unkown)),
        reclat = input.meteorite.reclat?.toDouble() ?: 0.0,
        reclong = input.meteorite.reclong?.toDouble() ?: 0.0
    )

    data class Input(val meteorite: Meteorite, val location: Location?, val context: Context)

}