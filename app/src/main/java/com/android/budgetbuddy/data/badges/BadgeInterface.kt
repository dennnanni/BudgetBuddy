package com.android.budgetbuddy.data.badges

import androidx.compose.runtime.Composable
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.data.database.User

interface BadgeInterface {
    fun verifyAchievement(user: User, transactions: List<Transaction>): Boolean

    @Composable
    fun DisplayBadge()
}