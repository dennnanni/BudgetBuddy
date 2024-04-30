package com.android.budgetbuddy.ui.screens.register

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

@Composable
fun RegisterScreen(navController: NavHostController, userState: UserState, actions: UserActions) {

    val fullName = rememberSaveable { mutableStateOf("") }
    val username = rememberSaveable { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }



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
                label = { Text("Full Name") },
                value = fullName.value,
                onValueChange = { fullName.value = it }, modifier = Modifier
                    .fillMaxWidth()
            )

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

            OutlinedTextField(
                label = { Text("Confirm password") },
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
                    actions.addUser(User(
                        name = fullName.value,
                        username = username.value,
                        password = password.value
                    ))
                    navController.navigate(BudgetBuddyRoute.Home.route)
                }
            ) {
                Text(text = "Register")
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.size(4.dp))

                ClickableText(
                    text = AnnotatedString("Login"),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                ) {
                    navController.navigate("login") {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }

}