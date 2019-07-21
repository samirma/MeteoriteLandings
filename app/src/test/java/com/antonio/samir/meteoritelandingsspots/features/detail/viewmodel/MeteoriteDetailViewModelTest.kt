package com.antonio.samir.meteoritelandingsspots.features.detail.viewmodel

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

    lateinit var viewModel: MeteoriteDetailViewModel

    lateinit var meteoriteService: MeteoriteServiceInterface

    @Before
    fun setUp() {
        meteoriteService = mock()

        viewModel = MeteoriteDetailViewModel(meteoriteService)

    }

    @Test
    fun loadMeteoriteSuccess() {

        val data = MutableLiveData<Meteorite>()

        data.value = Meteorite().apply {
            id = 123
        }

        `when`(meteoriteService.getMeteoriteById(ArgumentMatchers.anyString())).thenReturn(data)


        assertEquals(meteoriteService.getMeteoriteById(ArgumentMatchers.anyString())?.value, data.value)

    }
}