package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction

@Composable
fun TransactionAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        containerColor = MaterialTheme.colorScheme.background,
        icon = {
            Icon(icon, null)
        },
        iconContentColor = MaterialTheme.colorScheme.primary,
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Row(horizontalArrangement = Arrangement.Center) {
                Text(text = dialogText)
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}