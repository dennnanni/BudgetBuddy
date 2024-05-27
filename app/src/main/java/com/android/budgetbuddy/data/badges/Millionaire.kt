package com.android.budgetbuddy.data.badges

import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction

class Millionaire : AbstractBadge() {
    override val badgeName: String = "Genius millionaire playboy philanthropist"
    override val badgeDescription: String =
        "Your balance reached 1,000,000 dollars"
    override val badgeIcon: Int = 0
    override val badgeProgress: Int = 0
    override val badgeGoal: Int = 0
    override val image: Int = R.drawable._4537_ppl
    override val badgeLambda: (List<Transaction>) -> Boolean = filter@{ transactions ->

        var balance = 0.0
        transactions.forEach {
            if (it.isExpense) {
                balance -= it.amount
            } else {
                balance += it.amount
            }
        }

        balance>= 1000000
    }
}