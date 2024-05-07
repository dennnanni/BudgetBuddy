package com.android.budgetbuddy.ui.screens.home

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import com.android.budgetbuddy.ui.TransactionActions
import com.android.budgetbuddy.ui.TransactionViewModel
import com.android.budgetbuddy.ui.composables.TransactionItem
import com.android.budgetbuddy.ui.composables.rememberMarker
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
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer.ColumnProvider.Companion.series
import com.patrykandpatrick.vico.core.common.shader.DynamicShader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


const val TRANSACTION_PREVIEW_COUNT = 10

@SuppressLint("UnrememberedMutableState")
@Composable
fun HomeScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    transactionActions: TransactionActions,
    userActions: UserActions
) {
    val context = LocalContext.current
    var totalBalance = 0.0

    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", null)

    userActions.loadCurrentUser(username!!)
    val userId = userActions.getUserId() ?: return /* TODO: handle */
    transactionActions.loadUserTransactions(userId)
    val transactions = transactionActions.getUserTransactions(userId)

    val data = mutableMapOf<Float, Float>()

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
            listOf(rememberLineSpec(
                DynamicShader.color(MaterialTheme.colorScheme.primary),
                backgroundShader = null
            ))
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
                    text = "$totalBalance €",
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
                    val orderedList = transactions
                        .sortedByDescending { it.id }
                        .take(TRANSACTION_PREVIEW_COUNT)
                    items(orderedList) {
                        TransactionItem(it, navController)
                    }
                }
            }
        }
    }
}
