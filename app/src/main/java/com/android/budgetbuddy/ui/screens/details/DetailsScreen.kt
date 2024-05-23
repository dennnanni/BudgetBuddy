package com.android.budgetbuddy.ui.screens.details

import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.data.remote.OSMDataSource
import com.android.budgetbuddy.data.remote.OSMPlace
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.composables.LoadingAnimation
import com.android.budgetbuddy.ui.composables.TransactionAlertDialog
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.utils.Coordinates
import com.android.budgetbuddy.ui.utils.isOnline
import com.android.budgetbuddy.ui.utils.toLocaleString
import com.android.budgetbuddy.ui.viewmodel.TransactionActions
import kotlinx.coroutines.launch

@Composable
fun DetailsScreen(
    transaction: Transaction?,
    navController: NavController,
    currencyViewModel: CurrencyViewModel,
    transactionActions: TransactionActions,
    osmDataSource: OSMDataSource,
    userId: Int? = 0
) {
    val context = LocalContext.current

    var openDeleteDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var place: OSMPlace? by remember { mutableStateOf(null) }

    if (transaction != null) {
        val coordinates by remember {mutableStateOf(Coordinates(transaction.latitude, transaction.longitude))}

        LaunchedEffect(coordinates) {
            if (coordinates.latitude == 0.0 && coordinates.longitude == 0.0) return@LaunchedEffect
            if (!isOnline(context)) {
                return@LaunchedEffect
            }
            place = osmDataSource.getPlace(coordinates)
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
                        key = stringResource(R.string.date),
                        value = transaction.date.toString()
                    )
                    DetailRow(
                        key = stringResource(R.string.category),
                        value = transaction.category
                    )

                    Spacer(modifier = Modifier.padding(5.dp))

                    if (transaction.latitude != 0.0 && transaction.longitude != 0.0) {
                        Column {
                            Text(
                                text = stringResource(R.string.location),
                                style = TextStyle(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                )
                            )
                            Text(
                                text = if (place == null) {
                                    "(${transaction.latitude}, ${transaction.longitude})"
                                } else {
                                    place!!.displayName
                                },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

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

@Composable
fun DetailRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .padding(0.dp, 5.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = key,
            style = TextStyle(
                fontWeight = FontWeight.ExtraBold,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize
            )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = value,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}