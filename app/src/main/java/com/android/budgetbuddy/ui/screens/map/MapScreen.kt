package com.android.budgetbuddy.ui.screens.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.graphics.drawable.DrawableWrapperCompat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.utils.Coordinates
import com.android.budgetbuddy.ui.utils.LocationService
import com.android.budgetbuddy.ui.utils.PermissionStatus
import com.android.budgetbuddy.ui.utils.rememberPermission
import com.android.budgetbuddy.ui.utils.resizeDrawable
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel
import com.utsman.osmandcompose.rememberMapViewWithLifecycle
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

val LATITUDE = 44.13
val LONGITUDE = 12.23

@Composable
fun MapScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    locationService: LocationService,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current

    var locationAlreadyRequested by remember { mutableStateOf(false) }
    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
    var latitude by remember { mutableDoubleStateOf(0.0) }
    var longitude by remember { mutableDoubleStateOf(0.0) }

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
        latitude = locationService.coordinates!!.latitude
        longitude = locationService.coordinates!!.longitude
    }

    if (latitude != 0.0 || longitude != 0.0) {
        MapViewComposable(
            context = context,
            navController = navController,
            transactions = transactionViewModel.userTransactions,
            center = Coordinates(latitude, longitude)
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

@Composable
fun MapViewComposable(
    context: Context,
    transactions: List<Transaction>,
    navController: NavHostController,
    center: Coordinates = Coordinates(LATITUDE, LONGITUDE),
) {
    val mapView = rememberMapViewWithLifecycle()

    val markers = transactions.map { transaction ->
        Marker(mapView).apply {
            position = GeoPoint(transaction.latitude, transaction.longitude)
            setOnMarkerClickListener { _, _ ->
                navController.navigate(BudgetBuddyRoute.TransactionDetails.buildRoute(transaction.id.toString()))
                true
            }
            title = transaction.title
            icon = resizeDrawable(context, R.drawable.location_marker, 30, 30)
        }
    }


    AndroidView(
        factory = { mapView },
        update = { view ->
            // Update your MapView here
            view.setMultiTouchControls(true)
            view.controller.setZoom(15.0)
            view.controller.setCenter(GeoPoint(center.latitude, center.longitude))
            view.overlays.addAll(markers)
        }
    )
}