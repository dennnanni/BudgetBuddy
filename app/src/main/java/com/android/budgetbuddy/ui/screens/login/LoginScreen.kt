package com.android.budgetbuddy.ui.screens.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.screens.settings.ThemeViewModel
import com.android.budgetbuddy.ui.utils.SPConstants
import com.android.budgetbuddy.ui.utils.hashPassword
import com.android.budgetbuddy.ui.viewmodel.UserActions
import com.android.budgetbuddy.ui.viewmodel.UserState
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavHostController, actions: UserActions, themeViewModel: ThemeViewModel) {

    val username = rememberSaveable { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences(SPConstants.APP_NAME, Context.MODE_PRIVATE)
    var invalidCredentials by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (invalidCredentials) {
        Toast.makeText(context, context.getString(R.string.invalid_credentials), Toast.LENGTH_LONG).show()
        invalidCredentials = false
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
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp).padding(top = 100.dp)
        ) {
            Box(
                modifier = Modifier.height(100.dp)
            ) {
                Image(
                    painter = if (themeViewModel.isDarkTheme())
                        painterResource(id = R.drawable.app_light)
                    else
                        painterResource(id = R.drawable.app_dark),
                    contentScale = ContentScale.Fit,
                    contentDescription = stringResource(id = R.string.profile_picture),
                )
            }
            Spacer(modifier = Modifier.height(80.dp))

            OutlinedTextField(
                label = { Text(stringResource(id = R.string.username)) },
                value = username.value,
                onValueChange = { username.value = it }, modifier = Modifier
                    .fillMaxWidth()
            )

            OutlinedTextField(
                label = { Text(stringResource(id = R.string.password)) },
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
                    coroutineScope.launch {
                        actions.login(username.value.trim(), hashPassword(password.value.trim())).join()
                        if (actions.getLoggedUser() == null) {
                            invalidCredentials = true
                        }
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.login))
            }

            Spacer(modifier = Modifier.height(5.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.first_time),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.size(4.dp))

                ClickableText(
                    text = AnnotatedString(stringResource(id = R.string.register)),
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                ) {
                    navController.navigate(BudgetBuddyRoute.Register.route) {
                        popUpTo(navController.graph.id) {
                            inclusive = true
                        }
                    }
                }
            }
        }
    }
}