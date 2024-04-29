package com.android.budgetbuddy.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.budgetbuddy.ui.screens.addTransaction.AddTransactionScreen
import com.android.budgetbuddy.ui.screens.home.HomeScreen
import org.koin.androidx.compose.koinViewModel

sealed class BudgetBuddyRoute(
    val route: String,
    val title: String,
    val args: List<NamedNavArgument> = emptyList()
) {
    data object Home: BudgetBuddyRoute("home", "BudgetBuddy")
    data object AddTransaction: BudgetBuddyRoute("addTransaction", "Add Transaction")
    data object TransactionDetails: BudgetBuddyRoute(
        "transactionDetails/{transactionId}",
        "Transaction Details",
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "transactionDetails/$transactionId"
    }
    data object Settings: BudgetBuddyRoute("settings", "Settings")
    data object EditTransaction: BudgetBuddyRoute(
        "editTransaction/{transactionId}",
        "Edit Transaction",
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "editTransaction/$transactionId"
    }

    // TODO: add other routes here

    companion object {
        // TODO: add other routes here
        val routes = setOf(Home, AddTransaction, TransactionDetails, Settings, EditTransaction)
    }
}

@Composable
fun BudgetBuddyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val vm = koinViewModel<TransactionViewModel>()
    val state by vm.state.collectAsStateWithLifecycle()
    NavHost(
        navController = navController,
        startDestination = BudgetBuddyRoute.Home.route,
        modifier = modifier
    ) {

        with(BudgetBuddyRoute.Home) {
            composable(route) {
                HomeScreen(navController, state, vm.actions)
            }
        }

        with(BudgetBuddyRoute.AddTransaction) {
            composable(route) {
                AddTransactionScreen(navController, state, vm.actions)
            }
        }

        // TODO: add other screens here
    }
}