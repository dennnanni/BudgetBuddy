package com.android.budgetbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.android.budgetbuddy.ui.BudgetBuddyNavGraph
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.composables.BottomBar
import com.android.budgetbuddy.ui.composables.TopBar
import com.android.budgetbuddy.ui.screens.home.HomeScreen
import com.android.budgetbuddy.ui.theme.BudgetBuddyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetBuddyTheme {
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

                    Scaffold(
                        topBar = {
                            // TODO: Add top bar
                            TopBar(navController = navController, currentRoute = currentRoute)
                        },
                        bottomBar = {
                            // TODO: Add bottom bar
                            BottomBar(navController = navController)
                        }
                    ) { paddingValues ->
                        BudgetBuddyNavGraph(
                            navController = navController,
                            modifier = Modifier.padding(paddingValues)
                        )

                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BudgetBuddyTheme {
        HomeScreen(NavHostController(LocalContext.current))
    }
}