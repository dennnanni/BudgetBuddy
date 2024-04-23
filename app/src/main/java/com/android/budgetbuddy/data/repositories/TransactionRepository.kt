package com.android.budgetbuddy.data.repositories

import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.data.database.TransactionDAO
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDAO: TransactionDAO) {
    val transactions: Flow<List<Transaction>> = transactionDAO.getAll()

    suspend fun upsert(transaction: Transaction) = transactionDAO.upsert(transaction)
    suspend fun delete(transaction: Transaction) = transactionDAO.delete(transaction)
}