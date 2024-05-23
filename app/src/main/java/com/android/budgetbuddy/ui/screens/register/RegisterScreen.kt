package com.android.budgetbuddy.ui.screens.register

import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.User
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.utils.SPConstants
import com.android.budgetbuddy.ui.utils.hashPassword
import com.android.budgetbuddy.ui.viewmodel.UserActions
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.Base64
import java.util.Locale

@Composable
fun RegisterScreen(navController: NavHostController, actions: UserActions) {

    val fullName = rememberSaveable { mutableStateOf("") }
    val username = rememberSaveable { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val confirmPassword = remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(SPConstants.APP_NAME, Context.MODE_PRIVATE)
    val coroutineScope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    if (error != null) {
        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        error = null
    }

    if (actions.getLoggedUser() != null) {
        with(sharedPreferences.edit()) {
            putString(SPConstants.USERNAME, username.value.trim())
            putString(SPConstants.NAME, actions.getLoggedUser()?.name)
            putString(SPConstants.PROFILE_PIC, actions.getLoggedUser()?.profilePic)
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

                    error = registrationChecks(
                        context,
                        fullName.value,
                        username.value.lowercase(Locale.ROOT),
                        password.value,
                        confirmPassword.value
                    )

                    if (error == null) {
                        coroutineScope.launch {
                            actions.getUserByUsername(username.value)?.let {
                                error = context.getString(R.string.username_already_exists)
                                return@launch
                            }

                            actions.addUser(
                                User(
                                    name = fullName.value.trim(),
                                    username = username.value.trim(),
                                    password = hashPassword(password.value.trim())
                                )
                            ).join()
                            actions.loadCurrentUser(username.value).join()

                        }
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.register))
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
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

fun registrationChecks(
    context: Context,
    fullName: String,
    username: String,
    password: String,
    confirmPassword: String,
    usernameChecks: (String) -> Boolean = { true }
): String? {
    if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        return context.getString(R.string.fields_cannot_be_empty)
    }
    if (username.length < 4) {
        return context.getString(R.string.username_must_be_at_least_4_characters_long)
    }

    if (password.length < 6) {
        return context.getString(R.string.password_must_be_at_least_6_characters_long)

    }

    if (password != confirmPassword) {
        return context.getString(R.string.passwords_do_not_match)
    }

    if (!usernameChecks(username)) {
        return context.getString(R.string.invalid_username)
    }


    return null
}