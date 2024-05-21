package com.android.budgetbuddy.ui.screens.achievements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.android.budgetbuddy.data.badges.AllBadges
import com.android.budgetbuddy.ui.viewmodel.EarnedBadgeViewModel
import com.android.budgetbuddy.ui.viewmodel.UserViewModel

@Composable
fun AllBadgesScreen(
    navController: NavHostController,
    badgesViewModel: EarnedBadgeViewModel,
    userViewModel: UserViewModel
) {
    badgesViewModel.actions.loadEarnedBadges(userViewModel.actions.getUserId()!!)
    val earnedBadges = badgesViewModel.earnedBadges

    if (earnedBadges.value.isNotEmpty()) {
        LazyColumn(modifier = Modifier.padding(10.dp)) {
            items(earnedBadges.value.map { AllBadges.badges[it.badgeName] }) { badge ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            MaterialTheme.colorScheme.primary
                        )
                        .padding(horizontal = 10.dp, vertical = 10.dp)

                ) {
                    badge?.DisplayBadge()
                }

                Spacer(modifier = Modifier.padding(5.dp))
            }
        }
    } else {
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No badges earned yet!",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}