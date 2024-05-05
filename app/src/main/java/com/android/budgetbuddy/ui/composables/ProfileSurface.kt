package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.User

@Composable
fun ProfileHome(user: User, navController: NavHostController) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            navController.navigate("profile")
        }
    ) {
        Image(
            painter = painterResource(id = R.drawable.default_propic),
            contentDescription = "R.string.profile_picture.toString()",
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(50.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(text = user.name)
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
fun ProfileProfile(name: String, username: String, profilePic: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            Image(
                painter = painterResource(id = R.drawable.default_propic),
                contentDescription = "R.string.profile_picture.toString()",
                modifier = Modifier
                    .clip(CircleShape)
                    .size(150.dp)
            )
            IconButton(
                onClick = { /*TODO*/ },
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