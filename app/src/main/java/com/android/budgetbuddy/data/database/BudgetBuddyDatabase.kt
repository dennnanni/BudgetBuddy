package com.android.budgetbuddy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Transaction::class, User::class], version = 4)
@TypeConverters(Converters::class)
abstract class BudgetBuddyDatabase : RoomDatabase() {
    abstract fun transactionDAO(): TransactionDAO
}