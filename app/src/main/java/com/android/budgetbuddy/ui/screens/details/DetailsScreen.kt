package com.android.budgetbuddy.ui.screens.details

import android.content.Context
import android.media.Image
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction

@Composable
fun DetailsScreen(transaction: Transaction) {
    val context = LocalContext.current
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
                    contentDescription = context.getString(R.string.category_icon),
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

            DetailRow(context = context, key = context.getString(R.string.type), value = transaction.type)
            DetailRow(context = context, key = context.getString(R.string.amount), value = transaction.amount.toString())
            DetailRow(context = context, key = context.getString(R.string.date), value = transaction.date.toString())
            DetailRow(context = context, key = context.getString(R.string.category), value = transaction.category)

            Spacer(modifier = Modifier.padding(5.dp))

            Column {
                Text(
                    text = context.getString(R.string.description),
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