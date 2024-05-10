package com.android.budgetbuddy.data.repositories

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.android.budgetbuddy.data.database.Category
import com.android.budgetbuddy.data.database.CategoryDAO
import com.android.budgetbuddy.data.database.RegularTransaction
import com.android.budgetbuddy.data.database.RegularTransactionDAO
import com.android.budgetbuddy.data.database.Transaction
import com.android.budgetbuddy.data.database.TransactionDAO
import com.android.budgetbuddy.data.database.User
import com.android.budgetbuddy.data.database.UserDAO
import com.android.budgetbuddy.data.remote.RatesDataSource
import com.android.budgetbuddy.ui.screens.settings.Currency
import com.android.budgetbuddy.ui.screens.settings.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TransactionRepository(private val transactionDAO: TransactionDAO) {
    val transactions: Flow<List<Transaction>> = transactionDAO.getAll()

    suspend fun upsert(transaction: Transaction) = transactionDAO.upsert(transaction)
    suspend fun delete(transaction: Transaction) = transactionDAO.delete(transaction)
    suspend fun getMostPopularCategories(): List<String> = transactionDAO.getMostPopularCategories()
    suspend fun getUserTransactions(username: Int): List<Transaction> = transactionDAO.getUserTransactions(username)
    suspend fun nukeTable() = transactionDAO.nukeTable()
}

class UserRepository(private val userDAO: UserDAO) {
    val users: Flow<List<User>> = userDAO.getAll()

    suspend fun getUser(username: String): User = userDAO.getUser(username)
    suspend fun getUserId(username: String): Int = userDAO.getUserId(username)
    suspend fun upsert(user: User) = userDAO.upsert(user)
    suspend fun delete(user: User) = userDAO.delete(user)
    suspend fun login(username: String, password: String): User = userDAO.login(username, password)
}

class CategoryRepository(private val categoryDAO: CategoryDAO) {
    suspend fun getAll(userId: Int): List<Category> = categoryDAO.getAll(userId)
    suspend fun upsert(category: Category) = categoryDAO.upsert(category)
    suspend fun delete(category: Category) = categoryDAO.delete(category)
}


class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val THEME_KEY = stringPreferencesKey("theme")
    }
    val theme = dataStore.data.map { preferences ->
            try {
                Theme.valueOf(preferences[THEME_KEY] ?: Theme.System.toString())
            } catch (_: Exception) {
                Theme.System
            }
        }
    suspend fun setTheme(theme: Theme) = dataStore.edit { it[THEME_KEY] = theme.toString() }
}

class CurrencyRepository(private val dataStore: DataStore<Preferences>, private val ratesDataSource: RatesDataSource) {
    companion object {
        private val CURRENCY_KEY = stringPreferencesKey("currency")
    }
    val currency = dataStore.data.map { preferences ->
        try {
            Currency.valueOf(preferences[CURRENCY_KEY] ?: Currency.USD.toString())
        } catch (_: Exception) {
            Currency.USD
        }
    }

    suspend fun getCurrency(): Currency = currency.first()

    suspend fun setCurrency(currency: Currency) = dataStore.edit { it[CURRENCY_KEY] = currency.toString() }

    suspend fun getUpdatedRate(): Double? {
        Log.d("Pippo", "Chiamata: ${ratesDataSource.getExchangeRates().rates[currency.first().toString()]}")
        return ratesDataSource.getExchangeRates().rates[currency.first().toString()]
    }
}

class RegularTransactionRepository(private val regularTransactionDAO: RegularTransactionDAO) {
    val transactions: Flow<List<RegularTransaction>> = regularTransactionDAO.getAll()

    suspend fun upsert(transaction: RegularTransaction) = regularTransactionDAO.upsert(transaction)
    suspend fun delete(transaction: RegularTransaction) = regularTransactionDAO.delete(transaction)
    suspend fun getMostPopularCategories(): List<String> = regularTransactionDAO.getMostPopularCategories()
    suspend fun getUserTransactions(username: Int): List<RegularTransaction> = regularTransactionDAO.getUserTransactions(username)
    suspend fun nukeTable() = regularTransactionDAO.nukeTable()
}