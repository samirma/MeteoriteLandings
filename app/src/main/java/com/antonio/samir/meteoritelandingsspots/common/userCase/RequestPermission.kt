package com.antonio.samir.meteoritelandingsspots.common.userCase

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.antonio.samir.meteoritelandingsspots.common.ResultOf
import com.antonio.samir.meteoritelandingsspots.common.userCase.RequestPermission.Input
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.coroutines.sendSuspend
import com.fondesa.kpermissions.extension.permissionsBuilder
import kotlinx.coroutines.flow.flow

class RequestPermission : UserCaseBase<Input, ResultOf<Boolean>>() {

    override fun action(input: Input) = flow {
        try {
            val result =
                input.activity.permissionsBuilder(input.permissions[0], *input.permissions)
                    .build().sendSuspend()

            emit(ResultOf.Success(result.allGranted()))
        } catch (e: Exception) {
            Log.e(TAG, e.message, e)
            emit(ResultOf.Error(e))
        }
    }

    class Input(val activity: AppCompatActivity, vararg val permissions: String)

    companion object {
        private val TAG = RequestPermission::class.java.simpleName
    }

}