package com.antonio.samir.meteoritelandingsspots.features.detail.presenter

import com.antonio.samir.meteoritelandingsspots.features.detail.ui.MeteoriteDetailView
import com.antonio.samir.meteoritelandingsspots.service.repository.database.MeteoriteDao

class MeteoriteDetailPresenter(val view: MeteoriteDetailView, val meteoriteDao: MeteoriteDao) {

    fun loadMeteoriteById(meteoriteId: String) {
        meteoriteDao.getMeteoriteById(meteoriteId)
    }

}