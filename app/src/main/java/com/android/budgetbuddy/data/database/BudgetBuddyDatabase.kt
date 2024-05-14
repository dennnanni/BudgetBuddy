package com.android.budgetbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Transaction::class,
        User::class,
        Category::class,
        RegularTransaction::class,
        EarnedBadge::class
    ], version = 11
)
@TypeConverters(Converters::class)
abstract class BudgetBuddyDatabase : RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
    abstract fun userDAO(): UserDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun regularTransactionDAO(): RegularTransactionDAO
    abstract fun earnedBadgeDAO(): EarnedBadgeDAO
}