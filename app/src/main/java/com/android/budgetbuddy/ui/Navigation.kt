package com.android.budgetbuddy.ui

import android.content.Context
import androidx.compose.material3.SnackbarHostState
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
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.remote.OSMDataSource
import com.android.budgetbuddy.ui.screens.achievements.AllBadgesScreen
import com.android.budgetbuddy.ui.screens.addTransaction.AddRegularTransactionScreen
import com.android.budgetbuddy.ui.screens.addTransaction.AddTransactionScreen
import com.android.budgetbuddy.ui.screens.changeInfo.ChangeName
import com.android.budgetbuddy.ui.screens.changeInfo.ChangePassword
import com.android.budgetbuddy.ui.screens.changeInfo.ChangeUsername
import com.android.budgetbuddy.ui.screens.charts.ChartsScreen
import com.android.budgetbuddy.ui.screens.details.DetailsScreen
import com.android.budgetbuddy.ui.screens.details.RegularDetailsScreen
import com.android.budgetbuddy.ui.screens.home.HomeScreen
import com.android.budgetbuddy.ui.screens.login.LoginScreen
import com.android.budgetbuddy.ui.screens.map.MapScreen
import com.android.budgetbuddy.ui.screens.profile.ProfileScreen
import com.android.budgetbuddy.ui.screens.register.RegisterScreen
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.screens.settings.SettingsScreen
import com.android.budgetbuddy.ui.screens.settings.ThemeState
import com.android.budgetbuddy.ui.screens.settings.ThemeViewModel
import com.android.budgetbuddy.ui.screens.viewAll.AllRegularTransactionScreen
import com.android.budgetbuddy.ui.screens.viewAll.AllTransactionsScreen
import com.android.budgetbuddy.ui.utils.LocationService
import com.android.budgetbuddy.ui.utils.SPConstants
import com.android.budgetbuddy.ui.viewmodel.CategoryViewModel
import com.android.budgetbuddy.ui.viewmodel.EarnedBadgeViewModel
import com.android.budgetbuddy.ui.viewmodel.RegularTransactionViewModel
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

sealed class BudgetBuddyRoute(
    val route: String,
    val title: Int,
    val args: List<NamedNavArgument> = emptyList()
) {
    data object Home : BudgetBuddyRoute("home", R.string.app_name)
    data object AddTransaction : BudgetBuddyRoute("transactions/add", R.string.add_transaction)
    data object AddRegularTransaction :
        BudgetBuddyRoute("regular_transactions/add", R.string.add_regular_transaction)

    data object Profile : BudgetBuddyRoute("profile", R.string.profile)
    data object TransactionDetails : BudgetBuddyRoute(
        "transactions/{transactionId}",
        R.string.transaction_details,
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "transactions/$transactionId"
    }

    data object RegularTransactionDetails : BudgetBuddyRoute(
        "regular_transactions/{transactionId}",
        R.string.transaction_details,
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "regular_transactions/$transactionId"
    }

    data object Settings : BudgetBuddyRoute("settings", R.string.settings)
    data object EditTransaction : BudgetBuddyRoute(
        "transactions/edit/{transactionId}",
        R.string.edit_transaction,
        listOf(navArgument("transactionId") { type = NavType.StringType })
    ) {
        fun buildRoute(transactionId: String) = "transactions/edit/$transactionId"
    }

    data object Register : BudgetBuddyRoute("register", R.string.register)

    data object Login : BudgetBuddyRoute("login", R.string.login)

    data object Transactions : BudgetBuddyRoute("transactions", R.string.all_transactions)
    data object RegularTransactions :
        BudgetBuddyRoute("regular_transactions", R.string.regular_transactions)

    data object Map : BudgetBuddyRoute("map", R.string.map)
    data object Charts : BudgetBuddyRoute("charts", R.string.charts)
    data object ChangeName : BudgetBuddyRoute("change/name", R.string.change_name)
    data object ChangeUsername : BudgetBuddyRoute("change/username", R.string.change_username)
    data object ChangePassword : BudgetBuddyRoute("change/password", R.string.change_password)

    data object AllBadges : BudgetBuddyRoute("badges", R.string.badges)

    companion object {
        val routes = setOf(
            Register,
            Login,
            Home,
            Profile,
            AddTransaction,
            TransactionDetails,
            RegularTransactionDetails,
            EditTransaction,
            Settings,
            Transactions,
            RegularTransactions,
            AddRegularTransaction,
            Map,
            Charts,
            ChangeName,
            ChangeUsername,
            ChangePassword,
            AllBadges
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

    val earnedBadgeViewModel = koinViewModel<EarnedBadgeViewModel>()

    val currencyViewModel = koinViewModel<CurrencyViewModel>()
    val regularTransactionViewModel = koinViewModel<RegularTransactionViewModel>()

    val locationService = koinInject<LocationService>()
    val osmDataSource = koinInject<OSMDataSource>()

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
                    categoryActions,
                    transactionViewModel.actions,
                    userViewModel.actions,
                    snackbarHostState,
                    regularTransactionViewModel
                )
            }
        }

        with(BudgetBuddyRoute.AddTransaction) {
            composable(route) {
                AddTransactionScreen(
                    navController,
                    userViewModel,
                    transactionViewModel.actions,
                    categoryActions,
                    currencyViewModel,
                    snackbarHostState,
                    osmDataSource,
                    earnedBadgeViewModel
                )
            }
        }

        with(BudgetBuddyRoute.EditTransaction) {
            composable(route, args) { backStackEntry ->
                val transaction = transactionsState.transactions.find {
                    it.id == backStackEntry.arguments?.getString("transactionId")?.toInt()
                }
                AddTransactionScreen(
                    navController,
                    userViewModel,
                    transactionViewModel.actions,
                    categoryActions,
                    currencyViewModel,
                    snackbarHostState,
                    osmDataSource,
                    earnedBadgeViewModel,
                    transaction
                )
            }
        }

        with(BudgetBuddyRoute.AddRegularTransaction) {
            composable(route) {
                AddRegularTransactionScreen(
                    navController,
                    userViewModel,
                    regularTransactionViewModel.actions,
                    categoryActions,
                    currencyViewModel
                )
            }
        }

        with(BudgetBuddyRoute.Register) {
            composable(route) {
                RegisterScreen(navController, userViewModel.actions)
            }
        }

        with(BudgetBuddyRoute.Login) {
            composable(route) {
                LoginScreen(navController, userViewModel.actions)
            }
        }

        with(BudgetBuddyRoute.Settings) {
            composable(route) {
                SettingsScreen(
                    themeViewModel::changeTheme,
                    currencyViewModel::changeCurrency,
                    currencyViewModel::getCurrency,
                    themeState,
                    userViewModel.actions,
                    navController
                )
            }
        }

        with(BudgetBuddyRoute.Transactions) {
            composable(route) {
                AllTransactionsScreen(
                    navController,
                    transactionViewModel,
                    currencyViewModel,
                    categoryActions
                )
            }
        }

        with(BudgetBuddyRoute.RegularTransactions) {
            composable(route) {
                AllRegularTransactionScreen(
                    regularTransactionViewModel,
                    currencyViewModel,
                    categoryActions,
                    navController
                )
            }
        }

        with(BudgetBuddyRoute.Profile) {
            composable(route) {
                ProfileScreen(
                    navController,
                    transactionsState,
                    transactionViewModel.actions,
                    userState,
                    userViewModel.actions,
                    earnedBadgeViewModel,
                    categoryActions
                )
            }
        }
        with(BudgetBuddyRoute.TransactionDetails) {
            composable(route, args) { backStackEntry ->
                val transaction = transactionsState.transactions.find {
                    it.id == backStackEntry.arguments?.getString("transactionId")?.toInt()
                }
                DetailsScreen(
                    transaction,
                    navController,
                    currencyViewModel,
                    transactionViewModel.actions,
                    osmDataSource,
                    userViewModel.actions.getUserId()
                )
            }
        }

        with(BudgetBuddyRoute.RegularTransactionDetails) {
            composable(route, args) { backStackEntry ->
                val transaction = regularTransactionViewModel.userTransactions.find {
                    it.id == backStackEntry.arguments?.getString("transactionId")?.toInt()
                }
                RegularDetailsScreen(
                    transaction,
                    navController,
                    currencyViewModel,
                    regularTransactionViewModel.actions,
                    osmDataSource,
                    userViewModel.actions.getUserId()
                )
            }
        }

        with(BudgetBuddyRoute.Map) {
            composable(route) {
                MapScreen(
                    navController,
                    transactionViewModel,
                    locationService,
                    snackbarHostState
                )
            }
        }
        with(BudgetBuddyRoute.Charts) {
            composable(route) {
                ChartsScreen(
                    transactionViewModel,
                    categoryActions,
                    currencyViewModel
                )
            }
        }
        with(BudgetBuddyRoute.ChangeName) {
            composable(route) {
                ChangeName(
                    navController,
                    userViewModel.actions
                )
            }
        }
        with(BudgetBuddyRoute.ChangeUsername) {
            composable(route) {
                ChangeUsername(
                    navController,
                    userViewModel.actions
                )
            }
        }
        with(BudgetBuddyRoute.ChangePassword) {
            composable(route) {
                ChangePassword(
                    navController,
                    userViewModel.actions
                )
            }
        }

        with(BudgetBuddyRoute.AllBadges) {
            composable(route) {
                AllBadgesScreen(
                    navController,
                    earnedBadgeViewModel,
                    userViewModel
                )
            }
        }

    }
}