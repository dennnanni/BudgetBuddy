package com.android.budgetbuddy.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.android.budgetbuddy.ui.utils.rememberPermission
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.util.GeoPoint

@Composable
fun MapContent() {
    val context = LocalContext.current

    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(-6.3970066, 106.8224316)
        zoom = 12.0 // optional, default is 5.0
    }

    // add node
    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState
    )


}