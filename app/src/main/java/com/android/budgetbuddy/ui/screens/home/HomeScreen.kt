package com.android.budgetbuddy.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.android.budgetbuddy.TransactionList
import com.android.budgetbuddy.ui.TransactionActions
import com.android.budgetbuddy.ui.TransactionViewModel
import com.android.budgetbuddy.ui.TransactionsState
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModelState: TransactionsState,
    viewModelActions: TransactionActions
) {
    TransactionList(viewModelState, viewModelActions)
}