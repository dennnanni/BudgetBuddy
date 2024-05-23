package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.utils.toLocaleString

val iconList: List<ImageVector> = listOf(
    Icons.Filled.Add,
    Icons.Filled.MoreVert,
    Icons.Filled.List,
    Icons.Filled.Edit,
    Icons.Filled.Close,
)

@Composable
fun TransactionItem(
    transaction: Transaction,
    currencyViewModel: CurrencyViewModel,
    icon: String,
    navController: NavHostController,
    modifier: Modifier = Modifier.padding(10.dp)
) {

    val currency = currencyViewModel.getCurrency()

    Row(
        modifier = modifier
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

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(50.dp)
                    .background(MaterialTheme.colorScheme.secondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    IconsList.entries.first {
                        it.name
                            .lowercase()
                            .contains(icon.lowercase())
                    }.icon, null,
                    tint = MaterialTheme.colorScheme.onSecondary
                )
            }

            Text(text = transaction.title)
        }

        val sign = if (transaction.isExpense) "-" else "+"
        Text(text = "${sign}${currencyViewModel.convert(transaction.amount).toLocaleString()} ${currency.getSymbol()}")
    }
}