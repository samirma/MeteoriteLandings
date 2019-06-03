package com.antonio.samir.meteoritelandingsspots.features.detail.ui

import com.antonio.samir.meteoritelandingsspots.model.Meteorite

interface MeteoriteDetailView {
    fun initView(meteorite: Meteorite)
}