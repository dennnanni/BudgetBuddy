package com.android.budgetbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 3)
@TypeConverters(Converters::class)
abstract class BudgetBuddyDatabase : RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
}