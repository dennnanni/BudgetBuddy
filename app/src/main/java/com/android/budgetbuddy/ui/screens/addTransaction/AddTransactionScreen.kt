@file:OptIn(ExperimentalMaterial3Api::class)

package com.android.budgetbuddy.ui.screens.addTransaction

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
import androidx.compose.runtime.mutableFloatStateOf
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
import com.android.budgetbuddy.ui.composables.CustomDatePicker
import java.time.LocalDate

@Composable
fun AddTransactionScreen() {
    val options = listOf("Expense", "Income")
    var selectedOption by remember { mutableStateOf(options[0]) }
    val amount: MutableState<Float> = remember { mutableFloatStateOf(0f) }
    val date = remember { mutableStateOf(LocalDate.now()) }
    val expenseTypes = listOf("Groceries", "Drugs", "Bills")
    val selectOptions = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(selectOptions[0]) }
    val description = rememberSaveable { mutableStateOf("") }
    val title = rememberSaveable { mutableStateOf("") }


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
                value = if (amount.value == 0f) "" else amount.value.toString(),
                onValueChange = { amount.value = it.toFloatOrNull() ?: 0f },
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

            //AutoComplete()

            OutlinedTextField(
                label = { Text("Description") },
                value = description.value,
                onValueChange = { description.value = it }, modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )
        }
    }
}