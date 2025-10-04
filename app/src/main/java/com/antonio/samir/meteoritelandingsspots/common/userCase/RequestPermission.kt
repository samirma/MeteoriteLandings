package com.antonio.samir.meteoritelandingsspots.common.userCase

import android.util.Log
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.RequestPermission.Input
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class RequestPermission @Inject constructor() : UserCaseBase<Input, ResultOf<Boolean>>() {

    override fun action(input: Input) = flow {
        emit(ResultOf.Success(true))
    }

    class Input( vararg val permissions: String)

    companion object {
        private val TAG = RequestPermission::class.java.simpleName
    }

}