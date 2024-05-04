package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.BudgetBuddyRoute

@Composable
fun TransactionItem(transaction: Transaction, navController: NavHostController) {
    Row(
        modifier = Modifier.padding(10.dp)
            .fillMaxWidth()
            .clickable {
                navController.navigate(BudgetBuddyRoute.TransactionDetails.buildRoute(transaction.id.toString()))
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.default_propic),
                contentDescription = R.string.category_icon.toString(),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(50.dp)
            )

            Text(text = transaction.title)
        }

        val sign = if (transaction.isExpense) "-" else "+"
        Text(text = "${sign}${transaction.amount} €")
    }
}