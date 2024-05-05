package com.android.budgetbuddy.ui.screens.profile

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.TransactionActions
import com.android.budgetbuddy.ui.TransactionsState
import com.android.budgetbuddy.ui.composables.ProfileProfile
import com.android.budgetbuddy.ui.utils.saveImageToStorage
import com.android.budgetbuddy.ui.viewmodel.UserActions
import com.android.budgetbuddy.ui.viewmodel.UserState

@Composable
fun ProfileScreen(
    navController: NavHostController,
    transactionsState: TransactionsState,
    transactionActions: TransactionActions,
    userState: UserState,
    userActions: UserActions
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("BudgetBuddy", 0)
    val name = sharedPreferences.getString("name", null) ?: ""
    val username = sharedPreferences.getString("username", null) ?: ""
    val profilePic = sharedPreferences.getString("profilePic", null) ?: ""
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val newUri = saveImageToStorage(it, context.contentResolver, "image.jpg")
                var currentUser = userActions.getLoggedUser()
                currentUser?.profilePic = newUri.toString()

                userActions.addUser(currentUser!!)
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
    ) {
        ProfileProfile(name = name, username = username, profilePic = profilePic)

        Spacer(modifier = Modifier.size(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp),
        ) {
            Text(
                text = "Most expenses in", style = TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                transactionActions.loadMostPopularCategories()
                for (transaction in transactionActions.getMostPopularCategories()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)

                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.default_propic),
                            contentDescription = R.string.category_icon.toString(),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .size(50.dp)
                        )

                        Text(text = transaction, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.size(20.dp))

        Button(onClick = {
            with(sharedPreferences.edit()) {
                remove("username")
                remove("name")
                remove("profilePic")
                apply()
            }

            userActions.logout()

            navController.navigate("login") {
                popUpTo(navController.graph.id) {
                    inclusive = true
                }
            }

        }) {
            Text(text = "Logout")
        }

        Button(onClick = {
            photoPickerLauncher.launch("image/*")
        }) {
            Text(text = "Change Profile Picture")
        }
        
        AsyncImage(model = selectedImageUri, contentDescription = null )
    }

}

fun saveImageToInternalStorage(context: Context, uri: Uri) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val outputStream = context.openFileOutput("image.jpg", Context.MODE_PRIVATE)

    inputStream?.use { input ->
        outputStream.use { output ->
            input.copyTo(output)
        }
    }
}