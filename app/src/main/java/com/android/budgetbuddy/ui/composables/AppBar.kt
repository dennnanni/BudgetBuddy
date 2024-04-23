package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.BudgetBuddyRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    currentRoute: BudgetBuddyRoute
) {
    val context = LocalContext.current
    /*TopAppBar(
        title = {
            if(currentRoute.route == BudgetBuddyRoute.Home.route) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.Menu, context.getString(R.string.menu), modifier = Modifier.size(40.dp))
                    Icon(Icons.Filled.Person, context.getString(R.string.profile), modifier = Modifier.size(40.dp))
                }
            }
        },
        navigationIcon = {
            if(navController.previousBackStackEntry != null) {
                IconButton(
                    onClick = { navController.navigateUp() }
                ) {
                    Icon(Icons.Filled.ArrowBack, context.getString(R.string.back))
                }
            }
        },
        actions = {
            if(currentRoute.route == BudgetBuddyRoute.Home.route) {
                IconButton(
                    onClick = { *//*navController.navigate(BudgetBuddyRoute.Settings.route)*//* }
                ) {
                    Icon(Icons.Filled.Settings, context.getString(R.string.settings))
                }
            }
        },
    )*/
    TopAppBar(
        title = {
            if (currentRoute.route == BudgetBuddyRoute.Home.route){
                //ProfileSurface("Full Name", "@username")
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(text = "BudgetBuddy",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.padding(10.dp))
                }
            }
        },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back button"
                    )
                }
            }
        },
        actions = {
            if (currentRoute.route == BudgetBuddyRoute.Home.route){
                IconButton({ navController.navigate(BudgetBuddyRoute.Settings.route) }) {
                    Icon(Icons.Filled.Settings, "Go to settings")
                }
            }
        }
    )
}

@Composable
fun BottomBar(
    navController: NavHostController
) {
    val context = LocalContext.current
    BottomAppBar {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { /* TODO: open menu */ },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Filled.Menu, context.getString(R.string.menu))
            }

            Surface(
                modifier = Modifier
                    .size(60.dp)
            ) {
                IconButton(
                    onClick = { /*navController.navigate(BudgetBuddyRoute.AddTransaction.route)*/ },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Add,
                        "Add transaction",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            IconButton(
                onClick = { /*navController.navigate(BudgetBuddyRoute.Profile.route)*/ },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Filled.Person, context.getString(R.string.profile))
            }

        }
    }
}