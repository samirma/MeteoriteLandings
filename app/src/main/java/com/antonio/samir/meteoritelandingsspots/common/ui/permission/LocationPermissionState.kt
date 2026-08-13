package com.antonio.samir.meteoritelandingsspots.common.ui.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect

/** Outcome of asking for location access. */
enum class LocationPermissionStatus {
    GRANTED,

    /** Not granted yet, and the system will show the dialog if we ask. */
    DENIED,

    /**
     * Denied to the point where the system no longer shows a dialog. The only way forward is the
     * app's settings page.
     */
    PERMANENTLY_DENIED,
}

/**
 * Runtime location permission, backed by the platform Activity Result contract that Google's
 * "Compose and other libraries" guidance recommends.
 *
 * This replaces the old `RequestPermission` use case, which returned `Success(true)`
 * unconditionally and never showed a dialog — the reason location silently never worked.
 */
@Stable
class LocationPermissionState internal constructor(
    private val context: Context,
    private val statusState: MutableState<LocationPermissionStatus>,
    private val onRequest: () -> Unit,
) {
    val status: LocationPermissionStatus by statusState

    val isGranted: Boolean get() = status == LocationPermissionStatus.GRANTED

    /** Shows the system dialog, or does nothing if permission is already granted. */
    fun request() {
        if (!isGranted) onRequest()
    }

    /** Opens this app's settings page, the only route back from a permanent denial. */
    fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null),
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /** Re-reads the live permission state, e.g. after returning from Settings. */
    internal fun refresh() {
        if (context.isLocationGranted()) {
            statusState.value = LocationPermissionStatus.GRANTED
        }
    }
}

@Composable
fun rememberLocationPermissionState(): LocationPermissionState {
    val context = LocalContext.current
    val statusState = remember(context) {
        mutableStateOf(
            if (context.isLocationGranted()) {
                LocationPermissionStatus.GRANTED
            } else {
                LocationPermissionStatus.DENIED
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        statusState.value = when {
            results.values.any { it } -> LocationPermissionStatus.GRANTED
            // Once the user has hard-denied, the system stops showing the dialog and
            // shouldShowRequestPermissionRationale goes false. That is how the two denial
            // states are told apart.
            context.shouldShowLocationRationale() -> LocationPermissionStatus.DENIED
            else -> LocationPermissionStatus.PERMANENTLY_DENIED
        }
    }

    val state = remember(context) {
        LocationPermissionState(
            context = context,
            statusState = statusState,
            onRequest = { launcher.launch(LOCATION_PERMISSIONS) },
        )
    }

    // The user can grant permission in Settings and come back, so re-read on resume rather than
    // leaving the UI stuck on the rationale.
    LifecycleResumeEffect(state) {
        state.refresh()
        onPauseOrDispose { }
    }

    return state
}

private val LOCATION_PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

private fun Context.isLocationGranted(): Boolean = LOCATION_PERMISSIONS.any {
    ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}

private fun Context.shouldShowLocationRationale(): Boolean {
    val activity = findActivity() ?: return false
    return LOCATION_PERMISSIONS.any {
        ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
    }
}

/** `LocalContext` may hand back a wrapper rather than the Activity itself. */
private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
