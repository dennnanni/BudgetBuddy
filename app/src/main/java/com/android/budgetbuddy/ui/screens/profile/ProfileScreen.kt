package com.android.budgetbuddy.ui.screens.profile

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.viewmodel.TransactionActions
import com.android.budgetbuddy.ui.viewmodel.TransactionsState
import com.android.budgetbuddy.ui.composables.ProfileProfile
import com.android.budgetbuddy.ui.utils.SPConstants
import com.android.budgetbuddy.ui.utils.rememberCameraLauncher
import com.android.budgetbuddy.ui.utils.rememberPermission
import com.android.budgetbuddy.ui.utils.saveImageToStorage
import com.android.budgetbuddy.ui.viewmodel.UserActions
import com.android.budgetbuddy.ui.viewmodel.UserState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    transactionsState: TransactionsState,
    transactionActions: TransactionActions,
    userState: UserState,
    userActions: UserActions
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val sharedPreferences = context.getSharedPreferences(SPConstants.APP_NAME, 0)
    val name = sharedPreferences.getString(SPConstants.NAME, null) ?: ""
    val username = sharedPreferences.getString(SPConstants.USERNAME, null) ?: ""
    val profilePic = sharedPreferences.getString(SPConstants.PROFILE_PIC, null) ?: ""

    transactionActions.loadMostPopularCategories()

    if(userActions.getLoggedUser() == null){
        userActions.loadCurrentUser(username)
    }
    var selectedImageUri by remember {
        mutableStateOf<Uri?>(Uri.parse(profilePic))
    }

    val cameraLauncher = rememberCameraLauncher { imageUri ->
        val currentUser = userActions.getLoggedUser()
        currentUser?.profilePic = imageUri.toString()

        with(sharedPreferences.edit()) {
            putString(SPConstants.PROFILE_PIC, imageUri.toString())
            apply()
        }

        selectedImageUri = imageUri

        userActions.addUser(currentUser!!)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val newUri = saveImageToStorage(it, context.contentResolver)
                val currentUser = userActions.getLoggedUser()
                currentUser?.profilePic = newUri.toString()

                with(sharedPreferences.edit()) {
                    putString("profilePic", newUri.toString())
                    apply()
                }

                selectedImageUri = newUri

                userActions.addUser(currentUser!!)
            }
        }
    )


    val cameraPermission = rememberPermission(Manifest.permission.CAMERA) { status ->
        if (status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun takePicture() {
        if (cameraPermission.status.isGranted) {
            cameraLauncher.captureImage()
        } else {
            cameraPermission.launchPermissionRequest()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileProfile(name = name, username = username, profilePic = selectedImageUri.toString()) {
            // Show dialog to choose between camera and gallery
            showDialog = true
        }

        Spacer(modifier = Modifier.size(20.dp))

        if (transactionActions.getMostPopularCategories().isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
            ) {
                Text(
                    text = stringResource(id = R.string.most_expenses_in) + ":", style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(30.dp)
                ) {
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
        }
        Spacer(modifier = Modifier.size(20.dp))

        Button(onClick = {
            photoPickerLauncher.launch("image/*")
        }) {
            Text(text = "Change Profile Picture")
        }

    }

    if (showDialog) {

        BasicAlertDialog(
            onDismissRequest = { showDialog = false }
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(top = 30.dp, end = 16.dp, start = 16.dp, bottom = 16.dp)) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.choose_an_option),
                        textAlign = TextAlign.Center,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ){
                        TextButton(
                            onClick = {
                                showDialog = false
                                takePicture()
                            }
                        ) {
                            Text(stringResource(id = R.string.take_picture))
                        }
                        Spacer(modifier = Modifier.size(10.dp))
                        TextButton(
                            onClick = {
                                showDialog = false
                                photoPickerLauncher.launch("image/*")
                            }
                        ) {
                            Text(stringResource(id = R.string.choose_from_files))
                        }
                    }
                }
            }
        }

    }

}