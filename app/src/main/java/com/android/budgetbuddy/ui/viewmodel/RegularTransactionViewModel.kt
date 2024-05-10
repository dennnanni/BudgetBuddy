package com.android.budgetbuddy.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.data.database.RegularTransaction
import com.android.budgetbuddy.data.repositories.RegularTransactionRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RegularTransactionsState(val transactions: List<RegularTransaction>)

interface RegularTransactionActions {
    fun addTransaction(transaction: RegularTransaction): Job
    fun removeTransaction(transaction: RegularTransaction): Job

    fun loadUserTransactions(userId: Int): Job
    fun getUserTransactions(userId: Int): List<RegularTransaction>
    fun loadMostPopularCategories(): Job
    fun getMostPopularCategories(): List<String>

    fun nukeTable(): Job

}

class RegularTransactionViewModel(private val repository: RegularTransactionRepository) : ViewModel() {
    val state = repository.transactions.map { RegularTransactionsState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TransactionsState(emptyList())
    )

    var userTransactions by mutableStateOf<List<RegularTransaction>>(emptyList())

    var mostPopularCategories = mutableStateOf<List<String>>(emptyList())

    val actions = object : RegularTransactionActions {
        override fun addTransaction(transaction: RegularTransaction) = viewModelScope.launch {
            repository.upsert(transaction)
        }

        override fun removeTransaction(transaction: RegularTransaction) = viewModelScope.launch {
            repository.delete(transaction)
        }

        override fun loadUserTransactions(userId: Int): Job = viewModelScope.launch {
            userTransactions = repository.getUserTransactions(userId)
        }

        override fun getUserTransactions(userId: Int): List<RegularTransaction> {
            return userTransactions
        }

        override fun loadMostPopularCategories(): Job = viewModelScope.launch {
            mostPopularCategories.value = repository.getMostPopularCategories()
        }

        override fun getMostPopularCategories(): List<String> {
            return mostPopularCategories.value
        }

        override fun nukeTable(): Job = viewModelScope.launch {
            repository.nukeTable()
        }
    }
}