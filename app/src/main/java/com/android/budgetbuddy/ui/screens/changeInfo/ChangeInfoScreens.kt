package com.android.budgetbuddy.ui.screens.changeInfo

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.utils.SPConstants
import com.android.budgetbuddy.ui.utils.hashPassword
import com.android.budgetbuddy.ui.viewmodel.UserActions
import kotlinx.coroutines.launch

@Composable
fun ChangeName(
    navController: NavHostController,
    userActions: UserActions
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(SPConstants.APP_NAME, 0)
    val coroutineScope = rememberCoroutineScope()
    var showConfirmToast by remember { mutableStateOf(false) }
    val user = userActions.getLoggedUser()!!
    var name by remember { mutableStateOf("") }

    if (showConfirmToast) {
        Toast.makeText(
            LocalContext.current,
            stringResource(id = R.string.name_changed),
            Toast.LENGTH_SHORT).show()

        showConfirmToast = false

        with(sharedPreferences.edit()) {
            putString(SPConstants.NAME, name)
            apply()
        }

        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Change name
                Text(
                    text = stringResource(id = R.string.your_current_name, user.name)
                )

                OutlinedTextField(
                    label = { Text(stringResource(id = R.string.new_name)) },
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = {
                name = name.trim()
                if (name.isEmpty()) {
                    return@Button
                }

                coroutineScope.launch {
                    userActions.addUser(user.copy(name = name))
                    showConfirmToast = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.change_name))
        }
    }

}

@Composable
fun ChangeUsername(
    navController: NavHostController,
    userActions: UserActions
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(SPConstants.APP_NAME, 0)
    val coroutineScope = rememberCoroutineScope()
    var showConfirmToast by remember { mutableStateOf(false) }
    val user = userActions.getLoggedUser()!!
    var username by remember { mutableStateOf("") }

    if (showConfirmToast) {
        Toast.makeText(
            LocalContext.current,
            stringResource(id = R.string.username_changed),
            Toast.LENGTH_SHORT).show()

        showConfirmToast = false

        with(sharedPreferences.edit()) {
            putString(SPConstants.USERNAME, username)
            apply()
        }

        navController.popBackStack()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Change username
                Text(
                    text = stringResource(id = R.string.your_current_username, user.username)
                )

                OutlinedTextField(
                    label = { Text(stringResource(id = R.string.new_username)) },
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = {
                username = username.trim()
                if (username.isEmpty()) {
                    return@Button
                }

                coroutineScope.launch {
                    if (userActions.getUserByUsername(username) != null) {
                        return@launch
                    }
                    userActions.addUser(user.copy(username = username))
                    showConfirmToast = true
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.change_username)
            )
        }
    }

}

@Composable
fun ChangePassword(
    navController: NavHostController,
    userActions: UserActions
) {
    val coroutineScope = rememberCoroutineScope()
    var showConfirmToast by remember { mutableStateOf(false) }
    var showPasswordMismatchToast by remember { mutableStateOf(false) }
    val user = userActions.getLoggedUser()!!
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    if (showConfirmToast) {
        Toast.makeText(
            LocalContext.current,
            stringResource(id = R.string.password_changed),
            Toast.LENGTH_SHORT).show()

        showConfirmToast = false

        navController.popBackStack()
    }
    if (showPasswordMismatchToast) {
        Toast.makeText(
            LocalContext.current,
            stringResource(id = R.string.passwords_do_not_match),
            Toast.LENGTH_SHORT).show()

        showPasswordMismatchToast = false
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth()
            ) {

                OutlinedTextField(
                    label = { Text(stringResource(id = R.string.new_password)) },
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    label = { Text(stringResource(id = R.string.confirm_new_password)) },
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = {
                password = password.trim()
                confirmPassword = confirmPassword.trim()
                if (password.isEmpty() || confirmPassword.isEmpty()) {
                    return@Button
                }

                if (password != confirmPassword) {
                    showPasswordMismatchToast = true
                    return@Button
                }

                coroutineScope.launch {
                    password = hashPassword(password)
                    userActions.addUser(user.copy(password = password))
                    showConfirmToast = true
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(id = R.string.change_password)
            )
        }
    }
}
