package com.android.budgetbuddy.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.User

@Composable
fun ProfileHome(user: User) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.default_propic),
            contentDescription = R.string.profile_picture.toString(),
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
                .size(50.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(text = user.name)
            Text(text = "@${user.username}",
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}