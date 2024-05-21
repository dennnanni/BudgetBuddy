package com.android.budgetbuddy.ui.screens.charts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.composables.LineChart
import com.android.budgetbuddy.ui.composables.BarChart
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ChartsScreen(
    transactionViewModel: TransactionViewModel,
    categoryActions: CategoryActions
) {
    val context = LocalContext.current
    var totalBalance = 0.0
    val transactions = transactionViewModel.userTransactions
    val sortedTransactions = transactions.sortedBy { it.date }
    val dateList = transactions.map {
        it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    }.distinct().mapIndexed { index, date -> date to index }.toMap()

    // Create a map where each index is mapped to the sum of the balance for the corresponding date
    val indexToBalanceMap = mutableMapOf<Float, Float>()

    var currentIndex: Int

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

    val categoryIndexed = categoryActions.getCategories().mapIndexed { index, category -> category.name to index }.toMap()

    val incomeExpensePairMap = mutableMapOf<Int, Pair<Float, Float>>()
    currentIndex = 0
    sortedTransactions.groupBy { it.category }.forEach { (category, transactionsOnCategory) ->
        val categoryIndex = categoryIndexed[category] ?: 0
        val income = transactionsOnCategory.filter { it.type == context.getString(R.string.income) }.sumOf { it.amount }
        val expense = transactionsOnCategory.filter { it.type == context.getString(R.string.expense) }.sumOf { it.amount }
        incomeExpensePairMap[categoryIndex] = Pair(income.toFloat(), expense.toFloat())
    }

    val categoryValueFormatter = CartesianValueFormatter { x, _, _ ->
        categoryIndexed.filter { it.value == x.toInt() }.keys.firstOrNull().toString()
    }


    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Row {
            Text(
                text = stringResource(id = R.string.balance_progression),
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        )  {
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .then(if (indexToBalanceMap.size < 20) Modifier.padding(bottom = 10.dp) else Modifier)
                    .clip(RoundedCornerShape(30.dp))
                    .fillMaxSize()
            ){
                LineChart(data = indexToBalanceMap, transactions.size == 1, dateValueFormatter)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row {
            Text(
                text = stringResource(id = R.string.transactions_by_category),
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
        )  {
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 10.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .fillMaxSize()
            ){
                BarChart(data = incomeExpensePairMap, categoryValueFormatter)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

    }
}