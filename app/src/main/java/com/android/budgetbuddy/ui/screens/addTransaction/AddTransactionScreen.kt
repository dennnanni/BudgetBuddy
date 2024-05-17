package com.android.budgetbuddy.ui.screens.addTransaction

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.data.remote.OSMDataSource
import com.android.budgetbuddy.data.remote.OSMPlace
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.composables.AddCategory
import com.android.budgetbuddy.ui.composables.CustomDatePicker
import com.android.budgetbuddy.ui.composables.CustomDropDown
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.utils.LocationService
import com.android.budgetbuddy.ui.utils.PermissionStatus
import com.android.budgetbuddy.ui.utils.isOnline
import com.android.budgetbuddy.ui.utils.openWirelessSettings
import com.android.budgetbuddy.ui.utils.rememberPermission
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.TransactionActions
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Composable
fun AddTransactionScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    actions: TransactionActions,
    categoryActions: CategoryActions,
    currencyViewModel: CurrencyViewModel,
    snackbarHostState: SnackbarHostState,
    osmDataSource: OSMDataSource,
    transaction: Transaction? = null
) {
    val context = LocalContext.current
    val options = listOf(stringResource(id = R.string.expense), stringResource(id = R.string.income))
    val showDialog = remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }
    val locationService = remember { LocationService(context) }

    var selectedOptionText by remember { mutableStateOf("") }

    if (categoryActions.getCategories().isEmpty()) {
        showDialog.value = true
    } else if (selectedOptionText == "") {
        selectedOptionText = categoryActions.getCategories()[0].name
    }

    val amount: MutableState<Double> = remember { mutableDoubleStateOf(transaction?.amount ?: 0.0) }
    val tmp = if (transaction?.date == null) {
        LocalDate.now()
    } else {
        transaction.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }
    if (transaction != null && transaction.latitude != 0.0) {
        locationService.setLocation(transaction.latitude, transaction.longitude)
    }
    val date: MutableState<LocalDate> = remember { mutableStateOf(tmp) }
    val description = rememberSaveable { mutableStateOf(transaction?.description ?: "") }
    val title = rememberSaveable { mutableStateOf(transaction?.title ?: "") }

    val coroutineScope = rememberCoroutineScope()

    var showLocationDisabledAlert by remember { mutableStateOf(false) }
    var showPermissionDeniedAlert by remember { mutableStateOf(false) }
    var showPermissionPermanentlyDeniedSnackbar by remember { mutableStateOf(false) }
    var showNoInternetConnectivitySnackbar by remember { mutableStateOf(false) }
    var place: OSMPlace? by remember { mutableStateOf(null) }

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

    fun requestLocation() {
        if (locationPermission.status.isGranted) {
            locationService.requestCurrentLocation()
        } else {
            locationPermission.launchPermissionRequest()
        }
    }

    LaunchedEffect(locationService.isLocationEnabled) {
        showLocationDisabledAlert = locationService.isLocationEnabled == false
    }

    LaunchedEffect(locationService.coordinates) {
        if (locationService.coordinates == null) return@LaunchedEffect
        if (!isOnline(context)) {
            return@LaunchedEffect
        }
        place = osmDataSource.getPlace(locationService.coordinates!!)
    }


    if (showDialog.value) {
         AddCategory(
             categoryActions,
             onDismissRequest = {
                 showDialog.value = false
                 categoryActions.loadCategories(userViewModel.actions.getUserId()!!)
             },
             userViewModel
         )
    }

    // UI
    Column {

        Box(
            modifier = Modifier
                .padding(12.dp, 12.dp)
                .clip(shape = RoundedCornerShape(15.dp))
                .background(color = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp, 16.dp)

                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    label = { Text(stringResource(id = R.string.title)) },
                    value = title.value,
                    onValueChange = { title.value = it }, modifier = Modifier
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { selectedOption = text }
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = { selectedOption = text }
                            )
                            Text(
                                text = text,
                                fontSize = 20.sp,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if (amount.value == 0.0) "" else amount.value.toString(),
                    onValueChange = { amount.value = it.toDoubleOrNull() ?: 0.0 },
                    label = { Text(text = stringResource(id = R.string.amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                CustomDatePicker(
                    value = LocalDate.from(date.value),
                    onValueChange = { date.value = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(categoryActions.getCategories().isNotEmpty()) {
                        CustomDropDown(options = categoryActions.getCategories().map { it.name }, fun (it: String) {selectedOptionText = it})
                    }
                    IconButton(
                        onClick = { showDialog.value = true},
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }


                OutlinedTextField(
                    label = { Text(stringResource(R.string.description)) },
                    value = description.value,
                    onValueChange = { description.value = it }, modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.clickable {
                        if (place == null) requestLocation()
                        else {
                            place = null
                            locationService.resetLocation()
                        }
                    }
                ) {
                    Icon(if (place == null) Icons.Filled.AddLocationAlt else Icons.Filled.Close, contentDescription = null)
                    Text(
                        text = place?.displayName ?: stringResource(id = R.string.add_location)
                    )
                }
            }

        }

        Button(
            modifier = Modifier
                .padding(12.dp, 0.dp)
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                val userId = userViewModel.actions.getUserId() ?: return@Button

                coroutineScope.launch {
                    if (transaction == null) {
                        Log.d("Pippo", "selectedOptionText: $selectedOptionText")
                        actions.addTransaction(
                            Transaction(
                                title = title.value.trim(),
                                description = description.value.trim(),
                                type = selectedOption,
                                category = selectedOptionText,
                                amount = currencyViewModel.convertToUSD(amount.value),
                                date = Date.from(
                                    date.value.atStartOfDay(ZoneId.systemDefault()).toInstant()
                                ),
                                periodic = false,
                                latitude = locationService.coordinates?.latitude ?: 0.0,
                                longitude = locationService.coordinates?.longitude ?: 0.0,
                                userId = userId
                            )
                        ).join()
                    } else {
                        transaction.title = title.value.trim()
                        transaction.description = description.value.trim()
                        transaction.type = selectedOption
                        transaction.category = selectedOptionText
                        transaction.amount = currencyViewModel.convertToUSD(amount.value)
                        transaction.date = Date.from(
                            date.value.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        )
                        transaction.latitude = locationService.coordinates?.latitude ?: 0.0
                        transaction.longitude = locationService.coordinates?.longitude ?: 0.0
                        actions.addTransaction(transaction).join()
                    }
                    actions.loadUserTransactions(userId).join()
                    navController.navigate(BudgetBuddyRoute.Home.route) {
                        popUpTo(BudgetBuddyRoute.Home.route) {
                            inclusive = true
                        }
                    }
                }
            }
        ) {
            Text(text = stringResource(id =
                if (transaction == null) R.string.add_transaction
                else R.string.edit_transaction
            ), fontSize = 20.sp)
        }
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

    if (showNoInternetConnectivitySnackbar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                message = context.getString(R.string.no_internet_connectivity),
                actionLabel = context.getString(R.string.go_to_settings),
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                openWirelessSettings(context)
            }
            showNoInternetConnectivitySnackbar = false
        }
    }
}