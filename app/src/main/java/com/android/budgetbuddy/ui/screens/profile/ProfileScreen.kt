package com.android.budgetbuddy.ui.screens.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.android.budgetbuddy.ui.composables.ProfileProfile

@Composable
fun ProfileScreen(
    navController: NavHostController,
    transactionsState: TransactionsState,
    transactionActions: TransactionActions,
    userState: UserState,
    userActions: UserActions
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", 0)
    val name = sharedPreferences.getString("name", null) ?: ""
    val username = sharedPreferences.getString("username", null) ?: ""
    val profilePic = sharedPreferences.getString("profilePic", null) ?: ""

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally

    ) {
        ProfileProfile(name = name, username = username, profilePic = profilePic)

        Button(onClick = {
            with(sharedPreferences.edit()) {
                remove("username")
                remove("name")
                remove("profilePic")
                apply()
            }

            userActions.logout()

            navController.navigate("login") {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }

        }) {
            Text(text = "Logout")
        }
    }

}