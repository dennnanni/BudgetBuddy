package com.android.budgetbuddy.ui.screens.viewAll

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.ui.composables.TransactionItem
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel

@Composable
fun AllTransactionsScreen(
    navController: NavHostController,
    transactionViewModel: TransactionViewModel,
    currencyViewModel: CurrencyViewModel,
    categoryActions: CategoryActions
) {
    LazyColumn(
        modifier = Modifier.padding(10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.surface)

    ) {
        items(transactionViewModel.userTransactions) {
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