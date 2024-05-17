package com.android.budgetbuddy.ui.screens.viewAll

import android.util.Log
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.composables.CategoryFilter
import com.android.budgetbuddy.ui.composables.ExpandableSectionContainer
import com.android.budgetbuddy.ui.composables.RangeDateFilter
import com.android.budgetbuddy.ui.composables.TransactionItem
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

    var displayedTransactions: MutableList<Transaction>
        = remember { mutableStateListOf() }

    if (displayedTransactions.isEmpty()) {
        displayedTransactions.addAll(transactionViewModel.userTransactions)
    }

    var expanded by remember { mutableStateOf(false) }
    var dateEnabled by remember { mutableStateOf(false) }

    // Filters variables
    var startDate: LocalDate by remember { mutableStateOf(LocalDate.now().minusMonths(1)) }
    var endDate: LocalDate by remember { mutableStateOf(LocalDate.now()) }
    val selectedCategories = remember { mutableStateListOf<String>() }

    // Filters place
    Column {
        ExpandableSectionContainer(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            Column {
                // Date filter
                RangeDateFilter(
                    enabled = dateEnabled,
                    onSelectionChange = { dateEnabled = it },
                    startValue = startDate,
                    endValue = endDate,
                    onStartValueChange = { startDate = it },
                    onEndValueChange = { endDate = it })
                // Category filter
                CategoryFilter(categoryActions.getCategories().map {
                    it.name
                }, selectedCategories) {
                    if (selectedCategories.contains(it)) selectedCategories.remove(it)
                    else selectedCategories.add(it)
                    Log.d("Pippo", "selectedCategories: $selectedCategories and it: $it")
                }
                // Amount filter
                // AmountFilter()
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 5.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Button(onClick = {
                    displayedTransactions.clear()
                    displayedTransactions.addAll(applyFilters(
                        transactions = transactionViewModel.userTransactions,
                        startDate = if (dateEnabled) startDate else null,
                        endDate = if (dateEnabled) endDate else null,
                        selectedCategories = selectedCategories
                    ))
                }) {
                    Text(text = "Apply")
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
            displayedTransactions.sortedBy { it.date }.sortedByDescending { it.id }
            items(displayedTransactions) {
                TransactionItem(
                    transaction = it,
                    currencyViewModel = currencyViewModel,
                    icon = categoryActions.getCategoryIcon(it.category),
                    navController = navController,
                    modifier = Modifier.padding(16.dp, 10.dp)
                )
            }

        }
    }
}

fun applyFilters(
    transactions: List<Transaction>,
    startDate: LocalDate? = null,
    endDate: LocalDate? = null,
    selectedCategories: List<String> = emptyList(),
) : List<Transaction> {

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
            Log.d("Pippo", "Category: ${it.category} and selectedCategories: $selectedCategories")
            selectedCategories.contains(it.category)
        }
    }

    return list
}