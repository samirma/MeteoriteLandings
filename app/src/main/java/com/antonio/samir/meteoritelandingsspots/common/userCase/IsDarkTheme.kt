package com.antonio.samir.meteoritelandingsspots.common.userCase

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.data.repository.UIThemeRepository
import com.antonio.samir.meteoritelandingsspots.service.address.AddressServiceImpl
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class IsDarkTheme @Inject constructor(
    private val uiThemeRepository: UIThemeRepository
) : UserCaseBase<Unit, Boolean>() {

    companion object {
        private val TAG = IsDarkTheme::class.java.simpleName
    }

    override fun action(input: Unit) = uiThemeRepository
        .getTheme()
        .map { it.value }
        .onEach {
            Log.d(TAG, "isDarkTheme $it")
        }

}