package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Category
import com.android.budgetbuddy.ui.viewmodel.CategoryActions
import com.android.budgetbuddy.ui.viewmodel.UserViewModel

@Composable
fun AddCategory(
    categoryActions: CategoryActions,
    onDismissRequest: () -> Unit,
    userViewModel: UserViewModel,
    categoryAlreadyExists: () -> Unit
) {

    val categoryName = remember { mutableStateOf("") }
    var icon by remember { mutableStateOf("Add") }
    var color by remember {
        mutableStateOf(Color.Blue)
    }
    val showDialog = remember { mutableStateOf(false) }
    categoryActions.loadCategories(userViewModel.actions.getUserId()!!)

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                // from to add a category
                Text(
                    text = stringResource(id = R.string.add_category),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )

                // add category text field
                OutlinedTextField(
                    value = categoryName.value,
                    onValueChange = { categoryName.value = it },
                    label = { Text(stringResource(R.string.category_name)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )

                IconPicker(selectedIcon = icon, onIconSelected = {
                    icon = it
                })

                // button to trigger color picker dialog
                Button(
                    onClick = { showDialog.value = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color
                    )
                ) {
                    Text("Color")
                }


                if (showDialog.value) {
                    ColorPickerDialog(
                        initialColor = MaterialTheme.colorScheme.primary.toHex(),
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.toHex(),
                            MaterialTheme.colorScheme.secondary.toHex(),
                            MaterialTheme.colorScheme.tertiary.toHex(),
                            MaterialTheme.colorScheme.surface.toHex(),
                            MaterialTheme.colorScheme.onSurface.toHex(),
                            MaterialTheme.colorScheme.onPrimary.toHex(),
                            MaterialTheme.colorScheme.onSecondary.toHex(),
                            MaterialTheme.colorScheme.onTertiary.toHex(),
                        ),
                        onChoice = {
                            showDialog.value = false
                            color = Color(android.graphics.Color.parseColor(it))
                        }
                    )
                }

                // add category button
                Button(
                    onClick = {

                        if (categoryActions.getCategories().any { it.name == categoryName.value }) {
                            categoryAlreadyExists()
                            return@Button
                        }

                        categoryActions.addCategory(
                            Category(
                                name = categoryName.value.trim(),
                                icon = icon,
                                color = color.toHex(),
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
                    Text(stringResource(id = R.string.add_category))
                }
            }
        }
    }
}

private fun Color.toHex(): String {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255
    return String.format("#%02x%02x%02x", red.toInt(), green.toInt(), blue.toInt())
}
