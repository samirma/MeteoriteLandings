package com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.antonio.samir.meteoritelandingsspots.service.business.MeteoriteServiceInterface
import com.antonio.samir.meteoritelandingsspots.service.business.model.Meteorite
import com.nhaarman.mockitokotlin2.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import kotlin.test.assertEquals

class MeteoriteDetailViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Rule
    @JvmField
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var viewModel: MeteoriteDetailViewModel

    private lateinit var meteoriteService: MeteoriteServiceInterface

    private lateinit var location: Location

    @Before
    fun setUp() {
        meteoriteService = mock()

        location = mock()

        viewModel = MeteoriteDetailViewModel(meteoriteService)

    }

    @Test
    fun loadMeteoriteSuccess() {

        val data = MutableLiveData<Meteorite>()

        data.value = Meteorite().apply {
            id = 123
        }

        `when`(meteoriteService.getMeteoriteById(ArgumentMatchers.anyString())).thenReturn(data)

        viewModel.loadMeteorite(Meteorite().apply {
            id = 123
        })

        val meteorite = viewModel.getMeteorite().apply {
            observeForever {}
        }

        assertEquals(meteorite.value, data.value)

    }

    @Test
    fun getLocationSuccess() {
        `when`(meteoriteService.location).thenReturn(location)

        assertEquals(viewModel.getLocation(), location)

    }

}