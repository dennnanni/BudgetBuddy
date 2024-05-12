package com.android.budgetbuddy.ui.composables

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.android.budgetbuddy.R
import com.android.budgetbuddy.ui.BudgetBuddyRoute

@Composable
fun ProfileHome(
    name: String,
    username: String,
    profilePic: String,
    navController: NavHostController
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            navController.navigate(BudgetBuddyRoute.Profile.route)
        }
    ) {
        Image(
            painter = if (profilePic.isNotEmpty()) rememberAsyncImagePainter(
                model = Uri.parse(profilePic)) else painterResource(id = R.drawable.default_propic),
            contentDescription = stringResource(id = R.string.profile_picture),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(text = name)
            Text(
                text = "@${username}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun ProfileProfile(name: String, username: String, profilePic: String, launch: ManagedActivityResultLauncher<String, Uri?>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(
                    model = Uri.parse(profilePic)  // or ht
                ),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(id = R.string.profile_picture),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(150.dp)
            )
            IconButton(
                onClick = { launch.launch("image/*") },
                modifier = Modifier
                    .offset(x = 110.dp, y = 110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = name)
            Text(
                text = "@${username}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}