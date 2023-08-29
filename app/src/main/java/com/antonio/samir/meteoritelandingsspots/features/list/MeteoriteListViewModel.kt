package com.antonio.samir.meteoritelandingsspots.features.list

import android.annotation.SuppressLint
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.ResultOf.InProgress
import com.antonio.samir.meteoritelandingsspots.designsystem.ui.components.MeteoriteItemView
import com.antonio.samir.meteoritelandingsspots.features.list.MeteoristListState.UiContent
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.FetchMeteoriteList
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.GetMeteorites
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StartAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.StatusAddressRecover
import com.antonio.samir.meteoritelandingsspots.features.list.userCases.SwitchUITheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.launch


/**
 * Layer responsible for manage the interactions between the activity and the services
 */
@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModel(
    private val startAddressRecover: StartAddressRecover,
    private val statusAddressRecover: StatusAddressRecover,
    private val fetchMeteoriteList: FetchMeteoriteList,
    private val getMeteorites: GetMeteorites,
    private val switchUITheme: SwitchUITheme,
) : ViewModel() {

    @SuppressLint("StaticFieldLeak")
    private lateinit var activity: AppCompatActivity

    private val _navigateToMeteoriteDetail = MutableStateFlow<MeteoriteItemView?>(null)

    val navigateToMeteoriteDetail: StateFlow<MeteoriteItemView?> = _navigateToMeteoriteDetail

    private var _uiState = MutableStateFlow<MeteoristListState>(MeteoristListState.Loading)

    // UI state exposed to the UI
    val uiState: StateFlow<MeteoristListState> = _uiState

    private val _addressStatus = MutableStateFlow<ResultOf<Float>>(InProgress(0f))
    val addressStatus: StateFlow<ResultOf<Float>> = _addressStatus

    fun onDarkModeToggleClick() {
        viewModelScope.launch {
            switchUITheme(Unit).collect {
                Log.i(TAG, "SwitchUITheme ${it.javaClass}")
            }
        }
    }

    fun fetchMeteoriteList(activity: AppCompatActivity) {
        this.activity = activity
//        viewModelScope.launch(Dispatchers.Default) {
//            fetchMeteoriteList(Unit).collect { resultOf ->
//                _uiState.update {
//                    when (resultOf) {
//                        is Error -> UiMessage(R.string.general_error)
//                        is InProgress -> Loading
//                        is Success -> UiContent(meteorites = meteorites)
//                    }
//                }
//            }
//        }

        _uiState.value = UiContent(
            meteorites = getMeteorites(
                GetMeteorites.Input(
                    query = "",
                    activity = activity
                )
            )
        )

        viewModelScope.launch(Dispatchers.Default) {
            recoverAddressStatus().collect { resultOf: ResultOf<Float> ->
                _addressStatus.value = resultOf
            }
        }
    }

    private fun recoverAddressStatus() = startAddressRecover(Unit)
        .flatMapConcat { statusAddressRecover(it) }

    fun searchLocation(query: String) {
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

    fun onItemClicked(meteoriteItemView: MeteoriteItemView?) {
        _navigateToMeteoriteDetail.value = meteoriteItemView
    }

    companion object {
        private val TAG = MeteoriteListViewModel::class.java.simpleName
    }

}
