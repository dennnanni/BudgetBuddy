package com.android.budgetbuddy.ui.screens.login

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun LoginScreen(navController: NavHostController) {
    // TODO: Implement Login Screen

    Text("Login Screen")

    Row {
        Text(
            text = "Already have an account?",
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