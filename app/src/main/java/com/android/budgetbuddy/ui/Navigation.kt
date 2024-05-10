package com.android.budgetbuddy.ui

import android.app.Activity
import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.android.budgetbuddy.data.remote.RatesDataSource
import com.android.budgetbuddy.ui.screens.addTransaction.AddTransactionScreen
import com.android.budgetbuddy.ui.screens.details.DetailsScreen
import com.android.budgetbuddy.ui.screens.home.HomeScreen
import com.android.budgetbuddy.ui.screens.login.LoginScreen
import com.android.budgetbuddy.ui.screens.profile.ProfileScreen
import com.android.budgetbuddy.ui.screens.register.RegisterScreen
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.screens.settings.SettingsScreen
import com.android.budgetbuddy.ui.screens.settings.ThemeState
import com.android.budgetbuddy.ui.screens.settings.ThemeViewModel
import com.android.budgetbuddy.ui.screens.viewAll.AllTransactionsScreen
import com.android.budgetbuddy.ui.utils.SPConstants
import com.android.budgetbuddy.ui.viewmodel.CategoryViewModel
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

sealed class BudgetBuddyRoute(
    val route: String,
    val title: String,
    val args: List<NamedNavArgument> = emptyList()
) {
    data object Home : BudgetBuddyRoute("home", "BudgetBuddy")
    data object AddTransaction : BudgetBuddyRoute("transactions/add", "Add Transaction")
    data object Profile : BudgetBuddyRoute("profile", "Profile")
    data object TransactionDetails : BudgetBuddyRoute(
        "transactions/{transactionId}",
        "Transaction Details",
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "transactions/$transactionId"
    }

    data object Settings : BudgetBuddyRoute("settings", "Settings")
    data object EditTransaction : BudgetBuddyRoute(
        "transactions/edit/{transactionId}",
        "Edit Transaction",
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "transactions/edit/$transactionId"
    }

    data object Register : BudgetBuddyRoute("register", "Register")

    data object Login : BudgetBuddyRoute("login", "Login")

    data object Transactions : BudgetBuddyRoute("transactions", "All Transactions")


    // TODO: add other routes here

    companion object {
        // TODO: add other routes here
        val routes = setOf(
            Register,
            Login,
            Home,
            Profile,
            AddTransaction,
            TransactionDetails,
            EditTransaction,
            Settings,
            Transactions
        )
        val bottomBarRoutes = setOf(
            Home,
            Profile,
            Transactions
        )
    }
}

@Composable
fun BudgetBuddyNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    themeViewModel: ThemeViewModel,
    themeState: ThemeState,
    snackbarHostState: SnackbarHostState
) {
    val transactionViewModel = koinViewModel<TransactionViewModel>()
    val transactionsState by transactionViewModel.state.collectAsStateWithLifecycle()

    val userViewModel = koinViewModel<UserViewModel>()
    val userState by userViewModel.state.collectAsStateWithLifecycle()

    val categoryActions = koinViewModel<CategoryViewModel>().actions

    val currencyViewModel = koinViewModel<CurrencyViewModel>()

    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences(SPConstants.APP_NAME, Context.MODE_PRIVATE)
    val username = sharedPreferences.getString(SPConstants.USERNAME, null)

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
                HomeScreen(
                    navController,
                    currencyViewModel,
                    transactionViewModel,
                    categoryActions,
                    transactionViewModel.actions,
                    userViewModel.actions,
                    snackbarHostState)
            }
        }

        with(BudgetBuddyRoute.AddTransaction) {
            composable(route) {
                AddTransactionScreen(navController, userViewModel, transactionViewModel.actions, categoryActions)
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

        with(BudgetBuddyRoute.Settings) {
            composable(route) {
                SettingsScreen(
                    themeViewModel::changeTheme,
                    currencyViewModel::changeCurrency,
                    currencyViewModel::getCurrency,
                    themeState,
                    transactionViewModel.actions,
                    userViewModel.actions,
                    navController
                )
            }
        }

        with(BudgetBuddyRoute.Transactions) {
            composable(route) {
                AllTransactionsScreen(transactionViewModel, currencyViewModel)
            }
        }

        with(BudgetBuddyRoute.Profile) {
            composable(route) {
                ProfileScreen(
                    navController,
                    transactionsState,
                    transactionViewModel.actions,
                    userState,
                    userViewModel.actions
                )
            }
        }
        with(BudgetBuddyRoute.TransactionDetails) {
            composable(route, args) { backStackEntry ->
                val transaction = requireNotNull(transactionsState.transactions.find {
                    it.id == backStackEntry.arguments?.getString("transactionId")?.toInt()
                })
                DetailsScreen(transaction)
            }
        }

        // TODO: add other screens here
    }
}