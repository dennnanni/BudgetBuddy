package com.android.budgetbuddy.ui.screens.viewAll

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.android.budgetbuddy.ui.composables.TransactionItem
import com.android.budgetbuddy.ui.screens.settings.Currency
import com.android.budgetbuddy.ui.screens.settings.CurrencyState
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel

@Composable
fun AllTransactionsScreen(
    transactionViewModel: TransactionViewModel,
    currencyViewModel: CurrencyViewModel
) {
    /*LazyColumn {
        items(transactionViewModel.userTransactions) { transaction ->
            TransactionItem(
                transaction = transaction,
                currencyViewModel = currencyViewModel,
                icon = transaction.,
                navController = )
        }
    }*/
}