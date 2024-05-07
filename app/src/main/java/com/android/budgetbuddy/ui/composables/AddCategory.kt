package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.budgetbuddy.data.database.Category
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.UserViewModel

@Composable
fun AddCategory(categoryActions: CategoryActions, onDismissRequest: () -> Unit, userViewModel: UserViewModel) {

    val categoryName = remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("Filled.Add") }
    categoryActions.loadCategories(userViewModel.actions.getUserId()!!)

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                // from to add a category
                Text(
                    text = "Add a category",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

                // add category text field
                OutlinedTextField(
                    value = categoryName.value,
                    onValueChange = { categoryName.value = it },
                    label = { Text("Category Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                IconPicker(selectedIcon = icon, onIconSelected = {
                    icon = it
                })

                // add category button
                Button(
                    onClick = {
                        categoryActions.addCategory(
                            Category(
                                name = categoryName.value,
                                icon = icon,
                                userId = userViewModel.actions.getUserId()!!
                            ),
                            userViewModel.actions.getUserId()!!
                        )
                        onDismissRequest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Add Category")
                }
            }
        }
    }
}