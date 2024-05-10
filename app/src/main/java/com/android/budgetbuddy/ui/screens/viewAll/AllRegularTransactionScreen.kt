package com.android.budgetbuddy.ui.screens.viewAll

import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.viewmodel.RegularTransactionViewModel

@Composable
fun AllRegularTransactionScreen(viewModel: RegularTransactionViewModel, navController: NavHostController) {

    Button(onClick = { navController.navigate(BudgetBuddyRoute.AddRegularTransaction.route) }) {

    }
}