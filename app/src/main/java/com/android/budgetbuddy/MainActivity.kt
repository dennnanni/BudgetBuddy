package com.android.budgetbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.TransactionActions
import com.android.budgetbuddy.ui.TransactionViewModel
import com.android.budgetbuddy.ui.TransactionsState
import com.android.budgetbuddy.ui.theme.BudgetBuddyTheme
import org.koin.androidx.compose.koinViewModel
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BudgetBuddyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val vm = koinViewModel<TransactionViewModel>()
                    val state by vm.state.collectAsStateWithLifecycle()
                    TransactionList(state, vm.actions)
                }
            }
        }
    }
}


@Composable
fun TransactionList(
    state: TransactionsState,
    actions: TransactionActions
) {
    LazyColumn {
        item {
            AddTransactionField(
                onSubmit = { content -> actions.addTransaction(Transaction(
                    title = content,
                    description = "description",
                    type = "affitto",
                    amount = 20.15,
                    date = Date(),
                    periodic = false
                )) },
                modifier = Modifier.padding(16.dp)
            )
        }
        items(state.transactions) {
            TransactionItem(
                it,
                onDelete = { actions.removeTransaction(it) }
            )
        }
    }
}

@Composable
fun AddTransactionField(
    onSubmit: (content: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var content by remember { mutableStateOf("") }
    OutlinedTextField(
        value = content,
        onValueChange = { content = it },
        label = { Text("TODO") },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = {
                if (content.isBlank()) return@IconButton
                onSubmit(content)
                content = ""
            }) {
                Icon(Icons.Outlined.Add, "Add TODO")
            }
        },
    )
}

@Composable
fun TransactionItem(
    item: Transaction,
    onDelete: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .toggleable(
                value = false,
                onValueChange = { },
                role = Role.Checkbox
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = false, onCheckedChange = null)
        Text(
            item.title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp).weight(1F)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Close, "Remove TODO")
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BudgetBuddyTheme {
        Greeting("Android")
    }
}