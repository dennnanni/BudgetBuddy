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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.RegularTransaction
import com.android.budgetbuddy.ui.BudgetBuddyRoute
import com.android.budgetbuddy.ui.composables.AddCategory
import com.android.budgetbuddy.ui.composables.CustomDropDown
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.RegularTransactionActions
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.math.pow

@Composable
fun AddRegularTransactionScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    actions: RegularTransactionActions,
    categoryActions: CategoryActions
) {
    val options =
        listOf(stringResource(id = R.string.expense), stringResource(id = R.string.income))
    val periods = mapOf(
        "Day" to 8.64* 10.0.pow(7.0),
        "Week" to 6.048* 10.0.pow(8.0),
        "Month" to 2.628* 10.0.pow(9.0),
        "Year" to 3.154* 10.0.pow(10.0))
    val showDialog = remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf(options[0]) }

    var selectedOptionText by remember { mutableStateOf(String()) }
    var selectedOptionPeriod by remember { mutableStateOf(periods.keys.first()) }

    if (categoryActions.getCategories().isNotEmpty()) {
        selectedOptionText = categoryActions.getCategories()[0].name
    } else {
        showDialog.value = true
    }

    val amount: MutableState<Double> = remember { mutableDoubleStateOf(0.0) }
    val periodAmount: MutableState<Int> = remember { mutableIntStateOf(1) }
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

                Text(
                    text = "Every:",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(0.3f),
                        value = periodAmount.value.toString(),
                        onValueChange = { periodAmount.value = it.toIntOrNull() ?: 1 },
                        label = { Text(text = "Amount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    CustomDropDown(
                        options = periods.keys.toList(),
                        fun(it: String) { selectedOptionPeriod = it }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (categoryActions.getCategories().isNotEmpty()) {
                        CustomDropDown(
                            options = categoryActions.getCategories().map { it.name },
                            fun(it: String) { selectedOptionText = it })
                    }
                    IconButton(
                        onClick = { showDialog.value = true },
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
                        RegularTransaction(
                            title = title.value,
                            description = description.value,
                            type = selectedOption,
                            category = selectedOptionText,
                            amount = amount.value,
                            interval = (periods[selectedOptionPeriod]!!*periodAmount.value).toLong(),
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