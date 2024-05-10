package com.android.budgetbuddy

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.viewmodel.TransactionActions
import com.android.budgetbuddy.ui.viewmodel.TransactionsState
import com.android.budgetbuddy.ui.composables.NavigationDrawer
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.screens.settings.Theme
import com.android.budgetbuddy.ui.screens.settings.ThemeViewModel
import com.android.budgetbuddy.ui.theme.BudgetBuddyTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.Date

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("BudgetBuddy", MODE_PRIVATE)
        sharedPreferences.edit().remove("upToDateRate").apply()

        setContent {
            val themeViewModel = koinViewModel<ThemeViewModel>()
            val themeState by themeViewModel.state.collectAsStateWithLifecycle()

            BudgetBuddyTheme(
                darkTheme = when(themeState.theme) {
                    Theme.Light -> false
                    Theme.Dark -> true
                    Theme.System -> isSystemInDarkTheme()
                }
            ) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val navController = rememberNavController()
                    val backStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute by remember {
                        derivedStateOf {
                            BudgetBuddyRoute.routes.find {
                                it.route == backStackEntry?.destination?.route
                            } ?: BudgetBuddyRoute.Home
                        }
                    }
                    
                    NavigationDrawer(navController = navController, currentRoute = currentRoute,
                        themeViewModel = themeViewModel, themeState = themeState)

                }
            }
        }
    }
}