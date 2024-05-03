package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StackedLineChart
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.BudgetBuddyNavGraph
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import kotlinx.coroutines.launch

@Composable
fun NavigationDrawer(navController: NavHostController, currentRoute: BudgetBuddyRoute) {
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", 0)

    val colors = NavigationDrawerItemDefaults.colors(
        unselectedContainerColor = MaterialTheme.colorScheme.onPrimary,
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
                drawerContainerColor = MaterialTheme.colorScheme.onPrimary,
            ) {
                val name = sharedPreferences.getString("name", null) ?: ""
                val username = sharedPreferences.getString("username", null) ?: ""
                val profilePic = sharedPreferences.getString("profilePic", null) ?: ""

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

                Divider()
                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(context.getString(R.string.all_transactions)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.List, context.getString(R.string.all_transactions)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        navController.navigate(BudgetBuddyRoute.AllTransactions.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(context.getString(R.string.charts)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.StackedLineChart, context.getString(R.string.charts)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        //navController.navigate(BudgetBuddyRoute.Profile.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(context.getString(R.string.map)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Map, context.getString(R.string.map)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        //navController.navigate(BudgetBuddyRoute.Profile.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(context.getString(R.string.badges)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.EmojiEvents, context.getString(R.string.badges)) },
                    onClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        //navController.navigate(BudgetBuddyRoute.Badges.route)
                    }
                )

                NavigationDrawerItem(
                    colors = colors,
                    label = { Text(context.getString(R.string.settings)) },
                    selected = false,
                    icon = { Icon(Icons.Outlined.Settings, context.getString(R.string.settings)) },
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
        Scaffold(
            topBar = {
                val name = sharedPreferences.getString("name", null) ?: ""
                val username = sharedPreferences.getString("username", null) ?: ""
                val profilePic = sharedPreferences.getString("profilePic", null) ?: ""
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
                modifier = Modifier.padding(paddingValues)
            )

        }
    }
}