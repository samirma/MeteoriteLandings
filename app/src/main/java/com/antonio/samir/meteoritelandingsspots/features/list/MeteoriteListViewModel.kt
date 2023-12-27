package com.antonio.samir.meteoritelandingsspots.features.list

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoristListState.UiContent
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.SwitchUITheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MeteoriteListViewModel @Inject constructor(
    private val getMeteorites: GetMeteorites,
    private val switchUITheme: SwitchUITheme,
) : ViewModel() {

    private var _uiState = MutableStateFlow<MeteoristListState>(MeteoristListState.Loading)

    // UI state exposed to the UI
    val uiState: StateFlow<MeteoristListState> = _uiState

    fun onDarkModeToggleClick() {
        viewModelScope.launch {
            switchUITheme(Unit).collect {
                Log.i(TAG, "SwitchUITheme ${it.javaClass}")
            }
        }
    }

    fun fetchMeteoriteList(activity: AppCompatActivity) {

        _uiState.value = UiContent(
            meteorites = getMeteorites(
                GetMeteorites.Input(
                    query = "",
                    activity = activity
                )
            )
        )
    }


    fun searchLocation(query: String, activity: AppCompatActivity) {
        (_uiState.value as? UiContent)?.let {
            _uiState.value = UiContent(
                meteorites = getMeteorites(
                    GetMeteorites.Input(
                        query = query,
                        activity = activity
                    )
                )
            )
        }
    }

    companion object {
        private val TAG = MeteoriteListViewModel::class.java.simpleName
    }

}
