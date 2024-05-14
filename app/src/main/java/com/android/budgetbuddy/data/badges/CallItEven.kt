package com.android.budgetbuddy.data.badges

import android.icu.util.Calendar
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction

class CallItEven : AbstractBadge() {
    override val badgeName: String = "Call It Even"
    override val badgeDescription: String =
        "You have made an equal number of expenses and income transactions."
    override val badgeIcon: Int = 0
    override val badgeProgress: Int = 0
    override val badgeGoal: Int = 0
    override val image: Int = R.drawable._45271
    override val badgeLambda: (List<Transaction>) -> Boolean = filter@{ transactions ->
        val currentDate = Calendar.getInstance()

        val oneMonthAgo = currentDate.clone() as Calendar
        oneMonthAgo.add(Calendar.MONTH, -1)

        val transactionsLastMonth = transactions.filter { it.date >= oneMonthAgo.time }
        if (transactionsLastMonth.isEmpty()) {
            return@filter false
        }
        var incomeCount = 0.0
        var expenseCount = 0.0
        transactionsLastMonth.forEach {
            if (it.isExpense) {
                expenseCount += it.amount
            } else {
                incomeCount += it.amount
            }
        }

        incomeCount == expenseCount
    }
}