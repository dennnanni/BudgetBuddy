package com.android.budgetbuddy.data.badges

import android.icu.util.Calendar
import android.util.Log
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.database.Transaction
import java.util.Date

class StingyBadge : AbstractBadge() {
    override val badgeName: String = "Stingy Badge"
    override val badgeDescription: String =
        "You have spent less than 10% of your income in the last month."
    override val badgeIcon: Int = 0
    override val badgeProgress: Int = 0
    override val badgeGoal: Int = 0
    override val image: Int = R.drawable._45271
    override val badgeLambda: (List<Transaction>) -> Boolean = filter@{ transactions ->

        val transactionsLastMonth = transactions.filter {
            it.date >= firstDayOfPreviousMonth()
                    && it.date <= firstDayOfThisMonth()
        }
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
        Log.d("StingyBadge", "Income: $incomeCount, Expense: $expenseCount")
        expenseCount / incomeCount < 0.1
    }
}

fun firstDayOfPreviousMonth(): Date {
    val currentDate = Calendar.getInstance()
    currentDate.add(Calendar.MONTH, -1)
    currentDate.set(Calendar.DAY_OF_MONTH, 1)
    currentDate.set(Calendar.HOUR_OF_DAY, 0)
    currentDate.set(Calendar.MINUTE, 0)
    currentDate.set(Calendar.SECOND, 0)
    currentDate.set(Calendar.MILLISECOND, 0)
    return currentDate.time
}

fun firstDayOfThisMonth(): Date {
    val currentDate = Calendar.getInstance()
    currentDate.set(Calendar.DAY_OF_MONTH, 1)
    currentDate.set(Calendar.HOUR_OF_DAY, 0)
    currentDate.set(Calendar.MINUTE, 0)
    currentDate.set(Calendar.SECOND, 0)
    currentDate.set(Calendar.MILLISECOND, 0)
    return currentDate.time
}