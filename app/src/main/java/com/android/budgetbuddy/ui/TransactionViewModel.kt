package com.android.budgetbuddy.ui

import androidx.compose.runtime.mutableStateOf
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

    fun loadMostPopularCategories(): Job

    fun getMostPopularCategories(): List<String>

}

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {
    val state = repository.transactions.map { TransactionsState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TransactionsState(emptyList())
    )

    var mostPopularCategories = mutableStateOf<List<String>>(emptyList())

    val actions = object : TransactionActions {
        override fun addTransaction(transaction: Transaction) = viewModelScope.launch{
            repository.upsert(transaction)
        }

        override fun removeTransaction(transaction: Transaction) = viewModelScope.launch{
            repository.delete(transaction)
        }

        override fun loadMostPopularCategories(): Job = viewModelScope.launch {
            mostPopularCategories.value = repository.getMostPopularCategories()
        }

        override fun getMostPopularCategories(): List<String> {
            return mostPopularCategories.value
        }
    }
}