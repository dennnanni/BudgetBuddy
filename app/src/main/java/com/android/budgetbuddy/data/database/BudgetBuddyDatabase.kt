package com.android.budgetbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        Transaction::class,
        User::class,
        Category::class,
    RegularTransaction::class
    ], version = 10
)
@TypeConverters(Converters::class)
abstract class BudgetBuddyDatabase : RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
    abstract fun userDAO(): UserDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun regularTransactionDAO(): RegularTransactionDAO
}