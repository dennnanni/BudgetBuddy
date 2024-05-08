package com.android.budgetbuddy.ui.screens.register

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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.User
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.viewmodel.UserActions
import com.android.budgetbuddy.ui.viewmodel.UserState
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(navController: NavHostController, userState: UserState, actions: UserActions) {

    val fullName = rememberSaveable { mutableStateOf("") }
    val username = rememberSaveable { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", Context.MODE_PRIVATE)
    val coroutineScope = rememberCoroutineScope()

    if (actions.getLoggedUser() != null) {
        with(sharedPreferences.edit()) {
            putString("username", username.value)
            putString("name", actions.getLoggedUser()?.name)
            putString("profilePic", actions.getLoggedUser()?.profilePic)
            apply()
        }
        navController.navigate(BudgetBuddyRoute.Home.route) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp, 16.dp)
        ) {
            OutlinedTextField(
                label = { Text(stringResource(R.string.full_name)) },
                value = fullName.value,
                onValueChange = { fullName.value = it }, modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                label = { Text(stringResource(R.string.username)) },
                value = username.value,
                onValueChange = { username.value = it }, modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                label = { Text(stringResource(R.string.password)) },
                value = password.value,
                onValueChange = { password.value = it }, modifier = Modifier
                    .fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            OutlinedTextField(
                label = { Text(stringResource(R.string.confirm_password)) },
                value = confirmPassword.value,
                onValueChange = { confirmPassword.value = it }, modifier = Modifier
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
                    coroutineScope.launch {
                        actions.addUser(
                            User(
                                name = fullName.value,
                                username = username.value,
                                password = password.value
                            )
                        ).join()
                        actions.loadCurrentUser(username.value).join()
                        with(sharedPreferences.edit()) {
                            putString("username", username.value)
                            putString("name", actions.getLoggedUser()?.name)
                            putString("profilePic", actions.getLoggedUser()?.profilePic)
                            apply()
                        }
                        navController.navigate(BudgetBuddyRoute.Home.route)
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.register))
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row {
                Text(
                    text = stringResource(R.string.already_registered),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.size(4.dp))

                ClickableText(
                    text = AnnotatedString(stringResource(R.string.login)),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                ) {
                    navController.navigate(BudgetBuddyRoute.Login.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

}