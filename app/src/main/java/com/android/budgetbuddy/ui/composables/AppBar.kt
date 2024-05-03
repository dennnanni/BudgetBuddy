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
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.User
import com.android.budgetbuddy.ui.BudgetBuddyRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    currentRoute: BudgetBuddyRoute,
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", 0)
    val user = User(name = "Goku", username = "iamgoku", password = "", profilePic = "")

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White,
        ),
        title = {
            if (
                currentRoute.route == BudgetBuddyRoute.Home.route
                && currentRoute.route != BudgetBuddyRoute.Profile.route
            ) {
                    //ProfileHome(user)
            } else {
                val title: String =
                    sharedPreferences.getString("username", null) ?: currentRoute.title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = title,
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
            if (currentRoute.route == BudgetBuddyRoute.Home.route) {
                IconButton({ navController.navigate(BudgetBuddyRoute.Settings.route) }) {
                    Icon(
                        Icons.Outlined.Settings, context.getString(R.string.settings),
                        tint = MaterialTheme.colorScheme.primary
                    )
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
    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", 0)

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        tonalElevation = 0.dp,
        modifier = Modifier.clip(shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
    ) {
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
                    .clip(CircleShape)
            ) {
                IconButton(
                    onClick = { navController.navigate(BudgetBuddyRoute.AddTransaction.route) },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        "Add transaction",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            IconButton(
                onClick = {
                    /*with(sharedPreferences.edit()) {
                        remove("username")
                        apply()
                    }*/
                    navController.navigate(BudgetBuddyRoute.Profile.route)
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Filled.Person, context.getString(R.string.profile))
            }

        }
    }
}