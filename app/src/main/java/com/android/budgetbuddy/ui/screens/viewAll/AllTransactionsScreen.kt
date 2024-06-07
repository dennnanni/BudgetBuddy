package com.android.budgetbuddy.ui.screens.viewAll

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.composables.CategoryFilter
import com.android.budgetbuddy.ui.composables.ExpandableSectionContainer
import com.android.budgetbuddy.ui.composables.RangeDateFilter
import com.android.budgetbuddy.ui.composables.TransactionItem
import com.android.budgetbuddy.ui.composables.TypeFilter
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun AllTransactionsScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    currencyViewModel: CurrencyViewModel,
    categoryActions: CategoryActions
) {
    val context = LocalContext.current
    var noFilter by remember { mutableStateOf(true) }
    val displayedTransactions: MutableList<Transaction> = remember { mutableStateListOf() }

    if (displayedTransactions.isEmpty() && noFilter) {
        displayedTransactions.addAll(transactionViewModel.userTransactions)
    }

    var expanded by remember { mutableStateOf(false) }
    var dateEnabled by remember { mutableStateOf(false) }

    // Filters variables
    var startDate: LocalDate by remember { mutableStateOf(LocalDate.now().minusMonths(1)) }
    var endDate: LocalDate by remember { mutableStateOf(LocalDate.now()) }
    val selectedCategories = remember { mutableStateListOf<Pair<Int, String>>() }
    val selectedTypes = remember { mutableStateListOf<String>() }

    // Filters place
    Column {
        ExpandableSectionContainer(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            Column {
                // Date filter
                Text(text = stringResource(id = R.string.by_date))
                RangeDateFilter(
                    enabled = dateEnabled,
                    onSelectionChange = { dateEnabled = it },
                    startValue = startDate,
                    endValue = endDate,
                    onStartValueChange = { startDate = it },
                    onEndValueChange = { endDate = it })
                Text(text = stringResource(id = R.string.by_type))
                TypeFilter(
                    types = listOf(
                        stringResource(id = R.string.expense),
                        stringResource(id = R.string.income)
                    ),
                    selectedTypes = selectedTypes
                ) {
                    if (selectedTypes.contains(it)) selectedTypes.remove(it)
                    else selectedTypes.add(it)
                }
                Text(text = stringResource(id = R.string.by_category))
                // Category filter
                CategoryFilter(categoryActions.getCategories().map {
                    it.id to it.name
                }, selectedCategories) {
                    if (selectedCategories.contains(it)) selectedCategories.remove(it)
                    else selectedCategories.add(it)
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(0.5f),
                    onClick = {
                        displayedTransactions.clear()
                        displayedTransactions.addAll(
                            applyFilters(
                                context = context,
                                transactions = transactionViewModel.userTransactions,
                                startDate = if (dateEnabled) startDate else null,
                                endDate = if (dateEnabled) endDate else null,
                                selectedCategories = selectedCategories,
                                selectedTypes = selectedTypes
                            )
                        )
                        noFilter = false
                    }) {
                    Text(text = stringResource(id = R.string.apply))
                }

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        displayedTransactions.clear()
                        selectedTypes.clear()
                        selectedCategories.clear()
                        startDate = LocalDate.now().minusMonths(1)
                        endDate = LocalDate.now()
                        dateEnabled = false
                        noFilter = true
                    }) {
                    Text(text = stringResource(id = R.string.clear))
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surface)

        ) {
            val list = displayedTransactions
                .sortedByDescending { it.id }
                .sortedByDescending { it.date }
                .toMutableList()
            items(list) {
                TransactionItem(
                    transaction = it,
                    currencyViewModel = currencyViewModel,
                    icon = categoryActions.getCategoryIcon(it.category),
                    navController = navController,
                    color = categoryActions.getCategoryColor(it.category),
                    modifier = Modifier.padding(16.dp, 10.dp)
                )
            }

        }
    }
}

private fun applyFilters(
    context: Context,
    transactions: List<Transaction>,
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    selectedCategories: List<Pair<Int, String>> = emptyList(),
    selectedTypes: List<String> = emptyList()
): List<Transaction> {
    var list = transactions

    if (startDate != null && endDate != null) {
        // Filter by date
        list = list.filter {
            val date = it.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            date >= startDate && date <= endDate
        }
    }

    if (selectedCategories.isNotEmpty()) {
        // Filter by category
        list = list.filter {
            selectedCategories.map { pair -> pair.second }.contains(it.category)
        }
    }


    if (selectedTypes.isNotEmpty()) {
        // Filter by type
        list = list.filter {
            val type = if (it.type == "Expense") context.getString(R.string.expense)
            else context.getString(R.string.income)
            selectedTypes.contains(type)
        }
    }

    return list
}