package com.android.budgetbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.composables.NavigationDrawer
import com.android.budgetbuddy.ui.screens.settings.Theme
import com.android.budgetbuddy.ui.screens.settings.ThemeViewModel
import com.android.budgetbuddy.ui.theme.BudgetBuddyTheme
import com.android.budgetbuddy.ui.utils.SPConstants
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences(SPConstants.APP_NAME, MODE_PRIVATE)
        sharedPreferences.edit().remove(SPConstants.UP_TO_DATE_RATE).apply()
        sharedPreferences.edit().remove(SPConstants.TRIED_CONNECTION).apply()


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