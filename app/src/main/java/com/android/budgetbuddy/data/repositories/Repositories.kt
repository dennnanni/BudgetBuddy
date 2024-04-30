package com.android.budgetbuddy.data.repositories

import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.data.database.TransactionDAO
import com.android.budgetbuddy.data.database.User
import com.android.budgetbuddy.data.database.UserDAO
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDAO: TransactionDAO) {
    val transactions: Flow<List<Transaction>> = transactionDAO.getAll()

    suspend fun upsert(transaction: Transaction) = transactionDAO.upsert(transaction)
    suspend fun delete(transaction: Transaction) = transactionDAO.delete(transaction)
}

class UserRepository(private val userDAO: UserDAO) {
    val users: Flow<List<User>> = userDAO.getAll()

    suspend fun upsert(user: User) = userDAO.upsert(user)
    suspend fun delete(user: User) = userDAO.delete(user)
    suspend fun login(username: String, password: String): User = userDAO.login(username, password)
}