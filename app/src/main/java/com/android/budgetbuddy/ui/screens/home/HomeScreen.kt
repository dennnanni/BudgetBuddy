package com.android.budgetbuddy.ui.screens.home

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
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
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.composables.CartesianChart
import com.android.budgetbuddy.ui.composables.TransactionItem
import com.android.budgetbuddy.ui.composables.rememberMarker
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.utils.SPConstants
import com.android.budgetbuddy.ui.utils.isOnline
import com.android.budgetbuddy.ui.utils.openWirelessSettings
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.RegularTransactionViewModel
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
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date


const val TRANSACTION_PREVIEW_COUNT = 10

@Composable
fun HomeScreen(
    navController: NavHostController,
    currencyViewModel: CurrencyViewModel,
    transactionViewModel: TransactionViewModel,
    categoryActions: CategoryActions,
    transactionActions: TransactionActions,
    userActions: UserActions,
    snackbarHostState: SnackbarHostState,
    regularTransactionViewModel: RegularTransactionViewModel
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
    val userId = userActions.getLoggedUser()?.id
    if (userId == null) {
        Log.d("HomeScreen", "User ID is null")
        return
    }
    transactionActions.loadUserTransactions(userId)
    val transactions = transactionActions.getUserTransactions()

    regularTransactionViewModel.actions.loadUserTransactions(userId)
    val regularTransactions = regularTransactionViewModel.actions.getUserTransactions(userId)

    for (transaction in regularTransactions) {
        if (System.currentTimeMillis()
            > transaction.lastUpdate.time + transaction.interval
        ) {
            transactionActions.addTransaction(
                Transaction(
                    title = transaction.title,
                    description = transaction.description,
                    type = transaction.type,
                    category = transaction.category,
                    amount = transaction.amount,
                    periodic = true,
                    date = Date(transaction.lastUpdate.time + transaction.interval),
                    userId = userId
                )
            )

            transaction.lastUpdate = Date(transaction.lastUpdate.time + transaction.interval)
            regularTransactionViewModel.actions.addTransaction(transaction)
        }
    }

    categoryActions.loadCategories(userActions.getUserId()!!)

    val sortedTransactions = transactions.sortedBy { it.date }
    val dateList = transactions.map {
        it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }.distinct().mapIndexed { index, date -> date to index }.toMap()

    // Create a map where each index is mapped to the sum of the balance for the corresponding date
    val indexToBalanceMap = mutableMapOf<Float, Float>()

    var currentIndex = 0

    sortedTransactions.groupBy { it.date }.forEach { (date, transactionsOnDate) ->
        val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        currentIndex = dateList[localDate] ?: 0
        transactionsOnDate.forEach { transaction ->
            totalBalance += if (transaction.type == context.getString(R.string.income)) transaction.amount else -transaction.amount
        }
        indexToBalanceMap[currentIndex.toFloat()] = totalBalance.toFloat()
    }

    val dateValueFormatter = CartesianValueFormatter { x, _, _ ->
        dateList.filter { it.value == x.toInt() }.keys.firstOrNull()?.format(DateTimeFormatter.ofPattern("dd/MM")).toString()
    }

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
                    CartesianChart(indexToBalanceMap,
                        transactionViewModel.userTransactions.size == 1,
                        dateValueFormatter
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
                        .sortedBy { it.date }
                        .sortedByDescending { it.id }
                        .take(TRANSACTION_PREVIEW_COUNT)

                    items(orderedList) {

                        TransactionItem(
                            it,
                            currencyViewModel,
                            categoryActions.getCategoryIcon(it.category),
                            navController
                        )
                    }
                }
            }
        }
    }
}
