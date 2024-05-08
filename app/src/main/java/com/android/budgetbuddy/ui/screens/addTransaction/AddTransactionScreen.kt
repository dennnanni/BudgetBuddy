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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.viewmodel.TransactionActions
import com.android.budgetbuddy.ui.composables.AddCategory
import com.android.budgetbuddy.ui.composables.CustomDatePicker
import com.android.budgetbuddy.ui.composables.CustomDropDown
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    actions: TransactionActions,
    categoryActions: CategoryActions
) {
    val options = listOf("Expense", "Income")
    val showDialog = remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    var selectedOptionText by remember { mutableStateOf(String()) }

    if(categoryActions.getCategories().isNotEmpty()){
        selectedOptionText = categoryActions.getCategories()[0].name
    } else {
        showDialog.value = true
    }

    val amount: MutableState<Double> = remember { mutableDoubleStateOf(0.0) }
    val date = remember { mutableStateOf(LocalDate.now()) }
    var expanded by remember { mutableStateOf(false) }
    val description = rememberSaveable { mutableStateOf("") }
    val title = rememberSaveable { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()


    if (showDialog.value) {
         AddCategory(
             categoryActions,
             onDismissRequest = {
                 showDialog.value = false
                 categoryActions.loadCategories(userViewModel.actions.getUserId()!!)
             },
             userViewModel
         )
    }

    // UI
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if(categoryActions.getCategories().isNotEmpty()) {
                        CustomDropDown(options = categoryActions.getCategories().map { it.name }, fun (it: String) {selectedOptionText = it})
                    }
                    IconButton(
                        onClick = { showDialog.value = true},
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }


                OutlinedTextField(
                    label = { Text(stringResource(R.string.description)) },
                    value = description.value,
                    onValueChange = { description.value = it }, modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }


        }

        Button(
            modifier = Modifier
                .padding(12.dp, 0.dp)
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                val userId = userViewModel.actions.getUserId() ?: return@Button

                coroutineScope.launch {
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
                            periodic = false,
                            userId = userId
                        )
                    ).join()
                    actions.loadUserTransactions(userId).join()
                    navController.navigate(BudgetBuddyRoute.Home.route) {
                        popUpTo(BudgetBuddyRoute.Home.route) {
                            inclusive = true
                        }
                    }
                }
            }
        ) {
            Text(text = stringResource(id = R.string.add_transaction), fontSize = 20.sp)
        }
    }
}