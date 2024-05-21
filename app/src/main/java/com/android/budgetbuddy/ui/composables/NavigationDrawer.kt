package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.BudgetBuddyNavGraph
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.screens.settings.ThemeState
import com.android.budgetbuddy.ui.screens.settings.ThemeViewModel
import com.android.budgetbuddy.ui.utils.SPConstants
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(
    navController: NavHostController,
    currentRoute: BudgetBuddyRoute,
    themeViewModel: ThemeViewModel,
    themeState: ThemeState
) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences(SPConstants.APP_NAME, 0)

    val colors = NavigationDrawerItemDefaults.colors(
        unselectedContainerColor = MaterialTheme.colorScheme.background,
        selectedContainerColor = MaterialTheme.colorScheme.secondary,
        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
        selectedTextColor = MaterialTheme.colorScheme.onSecondary,
        unselectedIconColor = MaterialTheme.colorScheme.onBackground,
        selectedIconColor = MaterialTheme.colorScheme.onSecondary,
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = true,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(250.dp),
                drawerShape = RectangleShape,
                drawerContainerColor = MaterialTheme.colorScheme.background,
            ) {
                val name = sharedPreferences.getString(SPConstants.NAME, null) ?: ""
                val username = sharedPreferences.getString(SPConstants.USERNAME, null) ?: ""
                val profilePic = sharedPreferences.getString(SPConstants.PROFILE_PIC, null) ?: ""

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileHome(
                        name = name,
                        username = username,
                        profilePic = profilePic,
                        navController = navController
                    )
                }

                HorizontalDivider()

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(stringResource(R.string.all_transactions)) },
                    selected = false,
                    icon = { Icon(Icons.AutoMirrored.Outlined.List, stringResource(R.string.all_transactions)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(BudgetBuddyRoute.Transactions.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(stringResource(R.string.regular_transactions)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Repeat, stringResource(R.string.regular_transactions)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(BudgetBuddyRoute.RegularTransactions.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(stringResource(R.string.charts)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.StackedLineChart, stringResource(R.string.charts)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(BudgetBuddyRoute.Charts.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(stringResource(R.string.map)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Map, stringResource(R.string.map)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(BudgetBuddyRoute.Map.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(stringResource(R.string.badges)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.EmojiEvents, stringResource(R.string.badges)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(BudgetBuddyRoute.AllBadges.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(stringResource(R.string.settings)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Settings, stringResource(R.string.settings)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(BudgetBuddyRoute.Settings.route)
                    }
                )
            }
        }
    ) {

        val snackbarHostState = remember { SnackbarHostState() }


        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                val name = sharedPreferences.getString(SPConstants.NAME, null) ?: ""
                val username = sharedPreferences.getString(SPConstants.USERNAME, null) ?: ""
                val profilePic = sharedPreferences.getString(SPConstants.PROFILE_PIC, null) ?: ""
                TopBar(
                    navController = navController,
                    currentRoute = currentRoute,
                    name = name,
                    username = username,
                    profilePic = profilePic
                )
            },
            bottomBar = {
                if (currentRoute in BudgetBuddyRoute.bottomBarRoutes) {
                    BottomBar(navController = navController) {
                        coroutineScope.launch {
                            if (drawerState.isOpen) {
                                drawerState.close()
                            } else {
                                drawerState.open()
                            }
                        }
                    }
                }
            },

            ) { paddingValues ->
            BudgetBuddyNavGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                themeViewModel = themeViewModel,
                themeState = themeState,
                snackbarHostState = snackbarHostState
            )

        }
    }
}

