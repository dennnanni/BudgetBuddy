package com.android.budgetbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Transaction::class,
        User::class,
        Category::class,
        RegularTransactions::class,
        EarnedBadge::class
    ], version = 12
)
@TypeConverters(Converters::class)
abstract class BudgetBuddyDatabase : RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
    abstract fun userDAO(): UserDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun regularTransactionDAO(): RegularTransactionDAO
    abstract fun earnedBadgeDAO(): EarnedBadgeDAO
}