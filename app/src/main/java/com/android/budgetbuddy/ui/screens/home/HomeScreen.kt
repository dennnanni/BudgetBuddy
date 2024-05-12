package com.android.budgetbuddy.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.composables.TransactionItem
import com.android.budgetbuddy.ui.composables.rememberMarker
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.utils.SPConstants
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.TransactionActions
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel
import com.android.budgetbuddy.ui.viewmodel.UserActions
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottomAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStartAxis
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineSpec
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.shader.color
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


const val TRANSACTION_PREVIEW_COUNT = 10

@Composable
fun HomeScreen(
    navController: NavHostController,
    currencyViewModel: CurrencyViewModel,
    transactionViewModel: TransactionViewModel,
    categoryActions: CategoryActions,
    transactionActions: TransactionActions,
    userActions: UserActions,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    var totalBalance = 0.0

    val sharedPreferences = context.getSharedPreferences(SPConstants.APP_NAME, Context.MODE_PRIVATE)
    val username = sharedPreferences.getString(SPConstants.USERNAME, null) ?: return

    val upToDateRate = sharedPreferences.getBoolean(SPConstants.UP_TO_DATE_RATE, false)
    val alreadyTriedConnection = sharedPreferences.getBoolean(SPConstants.TRIED_CONNECTION, false)
    var showInternetRequiredSnackBar by remember { mutableStateOf(false) }
    var showConnectionIssuesSnackBar by remember { mutableStateOf(false) }

    /* Gestione dell'aggiornamento dei tassi di cambio in modo che venga
    visualizzata la snackbar una sola volta */
    if (!upToDateRate) {
        if (isOnline(context)) {
            LaunchedEffect(Unit) {
                currencyViewModel.updateRate().join()
                if (!currencyViewModel.defaultRate) {
                    sharedPreferences.edit().putBoolean(SPConstants.UP_TO_DATE_RATE, true).apply()
                } else if (!alreadyTriedConnection) {
                    showConnectionIssuesSnackBar = true
                }
            }
        } else if (!alreadyTriedConnection) {
            showInternetRequiredSnackBar = true
        }
        sharedPreferences.edit().putBoolean(SPConstants.TRIED_CONNECTION, true).apply()
    }

    if (showInternetRequiredSnackBar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                context.getString(R.string.internet_required_for_rate_update),
                context.getString(R.string.go_to_settings),
                duration = SnackbarDuration.Long
            )
            if (res == SnackbarResult.ActionPerformed) {
                openWirelessSettings(context)
            }
            if (res == SnackbarResult.Dismissed) {
                showInternetRequiredSnackBar = false
            }
        }
    }

    if (showConnectionIssuesSnackBar) {
        LaunchedEffect(snackbarHostState) {
            val res = snackbarHostState.showSnackbar(
                context.getString(R.string.unknown_communication_error),
                duration = SnackbarDuration.Long,
            )
            if (res == SnackbarResult.Dismissed) {
                showConnectionIssuesSnackBar = false
            }
        }
    }

    val currency = currencyViewModel.getCurrency()

    userActions.loadCurrentUser(username)
    val userId = userActions.getUserId() ?: return /* TODO: handle */
    transactionActions.loadUserTransactions(userId)
    val transactions = transactionActions.getUserTransactions(userId)

    val data = mutableMapOf<Float, Float>()
    categoryActions.loadCategories(userActions.getUserId()!!)

    var i = 0f
    for (transaction in transactionViewModel.userTransactions) {
        if (transaction.type == "Expense") {
            totalBalance -= transaction.amount
        } else {
            totalBalance += transaction.amount
        }
        data[i++] = totalBalance.toFloat()
    }

    // Chart settings
    val modelProducer = remember { CartesianChartModelProducer.build() }
    if (data.isNotEmpty()) {
        LaunchedEffect(Unit) {
            withContext(Dispatchers.Default) {
                modelProducer.tryRunTransaction {
                    lineSeries { series(data.keys, data.values) }
                }
            }
        }
    }

    val marker = rememberMarker()
    val cartesianChart = rememberCartesianChart(
        rememberLineCartesianLayer(
            listOf(
                rememberLineSpec(
                    DynamicShader.color(MaterialTheme.colorScheme.primary),
                    backgroundShader = null
                )
            )
        ),
        startAxis = rememberStartAxis(
            label = rememberAxisLabelComponent(color = MaterialTheme.colorScheme.onSurface),
            axis = rememberAxisLineComponent(color = MaterialTheme.colorScheme.onSurface),
            horizontalLabelPosition = VerticalAxis.HorizontalLabelPosition.Inside,
            guideline = rememberAxisGuidelineComponent(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
            )
        ),
        bottomAxis = rememberBottomAxis(
            axis = rememberAxisLineComponent(color = MaterialTheme.colorScheme.onSurface),
            label = null,
            guideline = null
        ),
    )
    val scrollState = rememberVicoScrollState(
        initialScroll = Scroll.Absolute.End,
        scrollEnabled = true,
    )

    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(45.dp),
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp, 15.dp, 30.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.total_balance),
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "${currencyViewModel.convert(totalBalance)} ${currency.getSymbol()}",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(color = MaterialTheme.colorScheme.surface)
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .background(color = MaterialTheme.colorScheme.surface)
                ) {
                    CartesianChartHost(
                        chart = cartesianChart,
                        scrollState = scrollState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 20.dp),
                        modelProducer = modelProducer,
                        marker = marker
                    )
                }
            }
        }


        if (transactions.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(16.dp, 10.dp, 16.dp, 5.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                ClickableText(
                    text = AnnotatedString(stringResource(R.string.see_all)),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                ) {
                    navController.navigate(BudgetBuddyRoute.Transactions.route)
                }
            }


            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(30.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                LazyColumn(
                    modifier = Modifier.padding(10.dp, 0.dp)
                ) {
                    val orderedList = transactionViewModel.userTransactions
                        .sortedByDescending { it.date }
                        .take(TRANSACTION_PREVIEW_COUNT)

                    items(orderedList) {

                        TransactionItem(it,
                            currencyViewModel,
                            categoryActions.getCategoryIcon(it.category),
                            navController)
                    }
                }
            }
        }
    }
}

fun isOnline(ctx: Context): Boolean {
    val connectivityManager = ctx.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true ||capabilities?.hasTransport(
        NetworkCapabilities.TRANSPORT_WIFI) == true
}

fun openWirelessSettings(ctx: Context) {
    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
        flags = Intent.
        FLAG_ACTIVITY_NEW_TASK
    }
    if (intent.resolveActivity(ctx.applicationContext.packageManager) != null) {
        ctx.applicationContext.startActivity(intent)
    }
}
