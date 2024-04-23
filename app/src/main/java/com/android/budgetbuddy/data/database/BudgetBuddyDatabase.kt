package com.android.budgetbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class], version = 2)
@TypeConverters(Converters::class)
abstract class BudgetBuddyDatabase : RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
}