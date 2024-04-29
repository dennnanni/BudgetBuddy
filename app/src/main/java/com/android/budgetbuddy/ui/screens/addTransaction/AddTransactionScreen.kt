@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.budgetbuddy.ui.screens.addTransaction

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.TransactionActions
import com.android.budgetbuddy.ui.TransactionsState
import com.android.budgetbuddy.ui.composables.CustomDatePicker
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@Composable
fun AddTransactionScreen(
    navController: NavHostController,
    state: TransactionsState,
    actions: TransactionActions
) {
    val options = listOf("Expense", "Income")
    val selectOptions = listOf("Groceries", "Drugs", "Bills")
    var selectedOption by remember { mutableStateOf(options[0]) }
    var selectedOptionText by remember { mutableStateOf(selectOptions[0]) }

    val amount: MutableState<Double> = remember { mutableDoubleStateOf(0.0) }
    val date = remember { mutableStateOf(LocalDate.now()) }
    var expanded by remember { mutableStateOf(false) }
    val description = rememberSaveable { mutableStateOf("") }
    val title = rememberSaveable { mutableStateOf("") }

    Column {

        Box(
            modifier = Modifier
                .padding(12.dp, 12.dp)
                .clip(shape = RoundedCornerShape(15.dp))
                .background(color = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp, 16.dp)

                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    label = { Text("Title") },
                    value = title.value,
                    onValueChange = { title.value = it }, modifier = Modifier
                        .fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    options.forEach { text ->
                        Row(
                            Modifier
                                .selectable(
                                    selected = (text == selectedOption),
                                    onClick = { selectedOption = text }
                                ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = (text == selectedOption),
                                onClick = { selectedOption = text }
                            )
                            Text(
                                text = text,
                                fontSize = 20.sp,
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = if (amount.value == 0.0) "" else amount.value.toString(),
                    onValueChange = { amount.value = it.toDoubleOrNull() ?: 0.0 },
                    label = { Text(text = "Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                CustomDatePicker(
                    value = date.value,
                    onValueChange = { date.value = it },
                    modifier = Modifier.fillMaxWidth()
                )

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        readOnly = true,
                        value = selectedOptionText,
                        onValueChange = { },
                        label = { Text("Category") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                        }
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = {
                            expanded = false
                        }
                    ) {
                        selectOptions.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(text = selectionOption) },
                                onClick = {
                                    selectedOptionText = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    label = { Text("Description") },
                    value = description.value,
                    onValueChange = { description.value = it }, modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }


        }
        Button(
            modifier = Modifier.padding(12.dp, 0.dp).fillMaxWidth().height(50.dp),
            onClick = {
                Log.d("AddTransactionScreen", "Title: ${title.value}")
                Log.d("AddTransactionScreen", "Selected Option: $selectedOption")
                Log.d("AddTransactionScreen", "Amount: ${amount.value}")
                Log.d("AddTransactionScreen", "Date: ${date.value}")
                Log.d("AddTransactionScreen", "Selected Option Text: $selectedOptionText")
                Log.d("AddTransactionScreen", "Description: ${description.value}")

                actions.addTransaction(
                    Transaction(
                        title = title.value,
                        description = description.value,
                        type = selectedOption,
                        category = selectedOptionText,
                        amount = amount.value,
                        date = Date.from(
                            date.value.atStartOfDay(ZoneId.systemDefault()).toInstant()
                        ),
                        periodic = false
                    )
                )
                navController.popBackStack()
            }
        ) {
            Text(text = "Add Transaction", fontSize = 20.sp)
        }
    }
}