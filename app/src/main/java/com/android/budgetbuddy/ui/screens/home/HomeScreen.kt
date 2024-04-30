package com.android.budgetbuddy.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.TransactionActions
import com.android.budgetbuddy.ui.TransactionsState

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModelState: TransactionsState,
    viewModelActions: TransactionActions
) {
    //TransactionList(viewModelState, viewModelActions)

    val context = LocalContext.current

    Column {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(30.dp),
            modifier = Modifier
                .height(300.dp)
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp, 15.dp, 15.dp, 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total Balance:",
                    style = MaterialTheme.typography.titleLarge
                )
                Text(text = "1234.56 €",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Box(modifier = Modifier
                .padding(15.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .fillMaxSize()
            ) {
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            ClickableText(
                text = AnnotatedString("See all"),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            ) {
                //navController.navigate("all_transactions")
            }
        }
    }
}