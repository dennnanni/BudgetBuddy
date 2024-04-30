package com.android.budgetbuddy.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.data.database.User
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.viewmodel.UserActions
import com.android.budgetbuddy.ui.viewmodel.UserState
import kotlinx.coroutines.delay

@Composable
fun LoginScreen(navController: NavHostController, userState: UserState, actions: UserActions) {

    val username = rememberSaveable { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", Context.MODE_PRIVATE)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp, 16.dp)
        ) {
            Text("Login Screen")

            OutlinedTextField(
                label = { Text("Username") },
                value = username.value,
                onValueChange = { username.value = it }, modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                label = { Text("Password") },
                value = password.value,
                onValueChange = { password.value = it }, modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                onClick = {
                    actions.login(username.value, password.value)

                    if (actions.getLoggedUser() != null) {
                        Log.d("LoginScreen", "User logged in")
                        with(sharedPreferences.edit()) {
                            putString("username", username.value)
                            apply()
                        }
                        navController.navigate(BudgetBuddyRoute.Home.route)
                    }
                }
            ) {
                Text(text = "Login")
            }

            Row {
                Text(
                    text = "First time?",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.size(4.dp))

                ClickableText(
                    text = AnnotatedString("Register"),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                ) {
                    navController.navigate("register") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
}