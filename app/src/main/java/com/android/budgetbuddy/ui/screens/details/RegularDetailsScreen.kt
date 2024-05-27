package com.android.budgetbuddy.ui.screens.details

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.RegularTransactions
import com.android.budgetbuddy.data.remote.OSMDataSource
import com.android.budgetbuddy.data.remote.OSMPlace
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.composables.TransactionAlertDialog
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.utils.toLocaleString
import com.android.budgetbuddy.ui.viewmodel.RegularTransactionActions
import kotlinx.coroutines.launch
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun RegularDetailsScreen(
    transaction: RegularTransactions?,
    navController: NavController,
    currencyViewModel: CurrencyViewModel,
    transactionActions: RegularTransactionActions,
    osmDataSource: OSMDataSource,
    userId: Int? = 0
) {
    val context = LocalContext.current

    var openDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var place: OSMPlace? by remember { mutableStateOf(null) }



    if (transaction != null) {
        var unit = "Days"
        val duration = transaction.interval
            .toDuration(DurationUnit.MILLISECONDS)
        var measure = duration.inWholeDays

        if(duration.inWholeDays >= 30 && (duration.inWholeDays % 30).toInt() == 0) {
            measure = duration.inWholeDays / 30
            unit = "Months"
        } else if(duration.inWholeDays >= 365 && (duration.inWholeDays % 365).toInt() == 0) {
            measure = duration.inWholeDays / 365
            unit = "Years"
        }


        when {
            openDeleteDialog -> {
                TransactionAlertDialog(
                    onDismissRequest = { openDeleteDialog = false },
                    onConfirmation = {
                        openDeleteDialog = false
                        coroutineScope.launch {
                            transactionActions.removeTransaction(transaction).join()
                            transactionActions.loadUserTransactions(userId!!).join()
                            navController.popBackStack()
                        }
                    },
                    dialogTitle = context.getString(R.string.delete_transaction, transaction.title),
                    dialogText = context.getString(R.string.delete_transaction_message),
                    icon = Icons.Default.WarningAmber
                )
            }
        }


        Column {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.default_propic),
                            contentDescription = stringResource(R.string.category_icon),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .size(50.dp)
                        )

                        Text(
                            text = transaction.title,
                            style = TextStyle(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize
                            )
                        )
                    }

                    Spacer(modifier = Modifier.padding(10.dp))

                    DetailRow(
                        key = stringResource(R.string.type),
                        value = transaction.type
                    )
                    DetailRow(
                        key = stringResource(R.string.amount),
                        value = "${currencyViewModel.convert(transaction.amount).toLocaleString()} ${
                            currencyViewModel.getCurrency().getSymbol()
                        }"
                    )
                    DetailRow(
                        key = stringResource(R.string.category),
                        value = transaction.category
                    )

                    DetailRow(
                        key = stringResource(R.string.interval),
                        value = "$measure $unit"
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    if (transaction.description.isNotEmpty()) {
                        Column {
                            Text(
                                text = stringResource(R.string.description),
                                style = TextStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                )
                            )

                            Text(
                                text = transaction.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .padding(16.dp, 0.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.size(150.dp, 40.dp),
                    onClick = {
                        navController.navigate(
                            BudgetBuddyRoute.EditTransaction.buildRoute(
                                transaction.id.toString()
                            )
                        )
                    }
                ) {
                    Text(
                        text = stringResource(R.string.edit),
                        style = TextStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    )
                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.size(150.dp, 40.dp),
                    onClick = {
                        openDeleteDialog = true
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        style = TextStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = MaterialTheme.typography.bodyLarge.fontSize
                        )
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //LoadingAnimation()
            Spacer(modifier = Modifier.height(10.dp))
            Text(stringResource(id = R.string.deleting_transaction))
        }
    }
}