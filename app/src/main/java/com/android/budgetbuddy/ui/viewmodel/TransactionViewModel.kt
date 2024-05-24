package com.android.budgetbuddy.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.data.repositories.TransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TransactionsState(val transactions: List<Transaction>)

interface TransactionActions {
    fun addTransaction(transaction: Transaction): Job
    fun removeTransaction(transaction: Transaction): Job

    fun loadUserTransactions(userId: Int): Job
    fun getUserTransactions(): List<Transaction>
    fun loadMostPopularCategories(userId: Int): Job
    fun getMostPopularCategories(): List<String>

    fun nukeTable(): Job

}

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
    val state = repository.transactions.map { TransactionsState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TransactionsState(emptyList())
    )

    var userTransactions by mutableStateOf<List<Transaction>>(emptyList())

    var mostPopularCategories = mutableStateOf<List<String>>(emptyList())

    val actions = object : TransactionActions {
        override fun addTransaction(transaction: Transaction) = viewModelScope.launch {
            repository.upsert(transaction)
        }

        override fun removeTransaction(transaction: Transaction) = viewModelScope.launch {
            repository.delete(transaction)
        }

        override fun loadUserTransactions(userId: Int): Job = viewModelScope.launch {
            userTransactions = repository.getUserTransactions(userId)
        }

        override fun getUserTransactions(): List<Transaction> {
            return userTransactions
        }

        override fun loadMostPopularCategories(userId: Int): Job = viewModelScope.launch {
            mostPopularCategories.value = repository.getMostPopularCategories(userId)
        }

        override fun getMostPopularCategories(): List<String> {
            return mostPopularCategories.value
        }

        override fun nukeTable(): Job = viewModelScope.launch {
            repository.nukeTable()
        }
    }
}