package com.android.budgetbuddy.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.android.budgetbuddy.ui.screens.addTransaction.AddTransactionScreen
import com.android.budgetbuddy.ui.screens.home.HomeScreen
import com.android.budgetbuddy.ui.screens.login.LoginScreen
import com.android.budgetbuddy.ui.screens.register.RegisterScreen
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

sealed class BudgetBuddyRoute(
    val route: String,
    val title: String,
    val args: List<NamedNavArgument> = emptyList()
) {
    data object Home : BudgetBuddyRoute("home", "BudgetBuddy")
    data object AddTransaction : BudgetBuddyRoute("addTransaction", "Add Transaction")
    data object TransactionDetails : BudgetBuddyRoute(
        "transactionDetails/{transactionId}",
        "Transaction Details",
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "transactionDetails/$transactionId"
    }

    data object Settings : BudgetBuddyRoute("settings", "Settings")
    data object EditTransaction : BudgetBuddyRoute(
        "editTransaction/{transactionId}",
        "Edit Transaction",
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "editTransaction/$transactionId"
    }

    data object Register : BudgetBuddyRoute("register", "Register")

    data object Login : BudgetBuddyRoute("login", "Login")

    // TODO: add other routes here

    companion object {
        // TODO: add other routes here
        val routes = setOf(
            Register,
            Login,
            Home,
            AddTransaction,
            TransactionDetails,
            EditTransaction,
            Settings
        )
        val bottomBarRoutes =
            setOf(Home, AddTransaction, TransactionDetails, EditTransaction, Settings)
    }
}

@Composable
fun BudgetBuddyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val transactionViewModel = koinViewModel<TransactionViewModel>()
    val transactionsState by transactionViewModel.state.collectAsStateWithLifecycle()

    val userViewModel = koinViewModel<UserViewModel>()
    val userState by userViewModel.state.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", null)

    val defaultRoute: String = if (username == null) {
        BudgetBuddyRoute.Register.route
    } else {
        BudgetBuddyRoute.Home.route
    }

    NavHost(
        navController = navController,
        startDestination = defaultRoute,
        modifier = modifier
    ) {

        with(BudgetBuddyRoute.Home) {
            composable(route) {
                HomeScreen(navController, transactionsState, transactionViewModel.actions)
            }
        }

        with(BudgetBuddyRoute.AddTransaction) {
            composable(route) {
                AddTransactionScreen(navController, transactionsState, transactionViewModel.actions)
            }
        }

        with(BudgetBuddyRoute.Register) {
            composable(route) {
                RegisterScreen(navController, userState, userViewModel.actions)
            }
        }

        with(BudgetBuddyRoute.Login) {
            composable(route) {
                LoginScreen(navController, userState, userViewModel.actions)
            }
        }

        // TODO: add other screens here
    }
}