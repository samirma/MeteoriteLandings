package com.antonio.samir.meteoritelandingsspots.features.list

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoristListState.UiContent
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StartAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StatusAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.SwitchUITheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@ExperimentalCoroutinesApi
class MeteoriteListViewModel @Inject constructor(
    private val startAddressRecover: StartAddressRecover,
    private val statusAddressRecover: StatusAddressRecover,
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

    private fun recoverAddressStatus() = startAddressRecover(Unit)
        .flatMapConcat { statusAddressRecover(it) }

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
