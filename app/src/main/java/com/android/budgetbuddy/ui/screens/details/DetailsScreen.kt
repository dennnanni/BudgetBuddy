package com.android.budgetbuddy.ui.screens.details

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction

@Composable
fun DetailsScreen(transaction: Transaction) {
    val context = LocalContext.current

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
                    context = context,
                    key = stringResource(R.string.type),
                    value = transaction.type
                )
                DetailRow(
                    context = context,
                    key = stringResource(R.string.amount),
                    value = "${transaction.amount} €"
                )
                DetailRow(
                    context = context,
                    key = stringResource(R.string.date),
                    value = transaction.date.toString()
                )
                DetailRow(
                    context = context,
                    key = stringResource(R.string.category),
                    value = transaction.category
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
                onClick = { /*TODO*/ }
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
                onClick = { /*TODO*/ }
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
}

@Composable
fun DetailRow(context: Context, key: String, value: String) {
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

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}