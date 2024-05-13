package com.android.budgetbuddy.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.remote.OSMDataSource
import com.android.budgetbuddy.ui.utils.Coordinates
import com.android.budgetbuddy.ui.utils.LocationService
import com.android.budgetbuddy.ui.utils.PermissionStatus
import com.android.budgetbuddy.ui.utils.openWirelessSettings
import com.android.budgetbuddy.ui.utils.rememberPermission
import com.android.budgetbuddy.ui.viewmodel.TransactionActions
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.util.GeoPoint

val LATITUDE = 44.13
val LONGITUDE = 12.23

@Composable
fun MapContent(
    transactionActions: TransactionActions,
    locationService: LocationService,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    var locationAlreadyRequested by remember { mutableStateOf(false) }
    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
    var place: Coordinates? by remember { mutableStateOf(null) }

    val locationPermission = rememberPermission(
        Manifest.permission.ACCESS_FINE_LOCATION
    ) { status ->
        when (status) {
            PermissionStatus.Granted ->
                locationService.requestCurrentLocation()

            PermissionStatus.Denied ->
                showPermissionDeniedAlert = true

            PermissionStatus.PermanentlyDenied ->
                showPermissionPermanentlyDeniedSnackbar = true

            PermissionStatus.Unknown -> {}
        }
    }

    LaunchedEffect(Unit) {
        if (!locationAlreadyRequested) {
            if (locationPermission.status.isGranted) {
                locationService.requestCurrentLocation()
            } else {
                locationPermission.launchPermissionRequest()
            }
            locationAlreadyRequested = true
        }
    }

    LaunchedEffect(locationService.isLocationEnabled) {
        showLocationDisabledAlert = locationService.isLocationEnabled == false
    }

    LaunchedEffect(locationService.coordinates) {
        if (locationService.coordinates == null) return@LaunchedEffect
        place = locationService.coordinates ?: Coordinates(LATITUDE, LONGITUDE)
    }

    // Creazione markers

    if (place != null) {
        val cameraState = rememberCameraState {
            geoPoint = GeoPoint(place?.latitude ?: LATITUDE, place?.longitude ?: LONGITUDE)
            zoom = 16.0 // optional, default is 5.0
        }

        Log.d("Pippo", "place: $place")

        // add node
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState
        )
    }


    if (showLocationDisabledAlert) {
        AlertDialog(
            title = { Text(stringResource(id = R.string.location_disabled)) },
            text = { Text(stringResource(id = R.string.location_permission_is_required_to_locate_on_map)) },
            confirmButton = {
                TextButton(onClick = {
                    locationService.openLocationSettings()
                    showLocationDisabledAlert = false
                }) {
                    Text(stringResource(id = R.string.enable))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDisabledAlert = false }) {
                    Text(stringResource(id = R.string.dismiss))
                }
            },
            onDismissRequest = { showLocationDisabledAlert = false }
        )
    }

    if (showPermissionDeniedAlert) {
        AlertDialog(
            title = { Text(stringResource(id = R.string.permission_denied)) },
            text = { Text(stringResource(id = R.string.location_permission_is_required_to_locate_on_map)) },
            confirmButton = {
                TextButton(onClick = {
                    locationPermission.launchPermissionRequest()
                    showPermissionDeniedAlert = false
                }) {
                    Text(stringResource(id = R.string.grant))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDeniedAlert = false }) {
                    Text(stringResource(id = R.string.dismiss))
                }
            },
            onDismissRequest = { showPermissionDeniedAlert = false }
        )
    }

    if (showPermissionPermanentlyDeniedSnackbar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                context.getString(R.string.location_permission_is_required_to_locate_on_map),
                context.getString(R.string.go_to_settings),
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                context.startActivity(
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                )
            }
            showPermissionPermanentlyDeniedSnackbar = false
        }
    }
}