package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.viewmodel.CategoryActions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPicker(
    options: List<String>,
    function: (String) -> Unit,
    categoryActions: CategoryActions,
    userId: Int,
    selected: String?
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(selected ?: options[0]) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(),
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text(stringResource(R.string.category)) },
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
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = selectionOption)
                            IconButton(onClick = { categoryActions.deleteCategory(selectionOption, userId) }) {
                                Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                            }
                        }
                    },
                    onClick = {
                        selectedOptionText = selectionOption
                        expanded = false
                        function(selectionOption)
                    }
                )
            }
        }
    }
}