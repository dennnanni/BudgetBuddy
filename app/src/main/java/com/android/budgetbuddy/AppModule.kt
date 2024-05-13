package com.android.budgetbuddy

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.android.budgetbuddy.data.database.BudgetBuddyDatabase
import com.android.budgetbuddy.data.remote.OSMDataSource
import com.android.budgetbuddy.data.remote.RatesDataSource
import com.android.budgetbuddy.data.repositories.CategoryRepository
import com.android.budgetbuddy.data.repositories.CurrencyRepository
import com.android.budgetbuddy.data.repositories.RegularTransactionRepository
import com.android.budgetbuddy.data.repositories.ThemeRepository
import com.android.budgetbuddy.data.repositories.TransactionRepository
import com.android.budgetbuddy.data.repositories.UserRepository
import com.android.budgetbuddy.ui.screens.settings.CurrencyViewModel
import com.android.budgetbuddy.ui.screens.settings.ThemeViewModel
import com.android.budgetbuddy.ui.viewmodel.CategoryViewModel
import com.android.budgetbuddy.ui.viewmodel.RegularTransactionViewModel
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import com.android.budgetbuddy.ui.utils.LocationService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("theme")

val appModule = module {
    single { get<Context>().dataStore }
    single {
        Room.databaseBuilder(
            get(),
            BudgetBuddyDatabase::class.java,
            "budgetbuddy"
        )
            // Sconsigliato per progetti seri! Lo usiamo solo qui per semplicità
            .fallbackToDestructiveMigration()
            .build()
    }
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                    })
            }
        }
    }
    single { LocationService(get()) }
    single { OSMDataSource(get()) }
    single { RatesDataSource(get()) }

    single { TransactionRepository(get<BudgetBuddyDatabase>().transactionDAO()) }
    single { RegularTransactionRepository(get<BudgetBuddyDatabase>().regularTransactionDAO()) }
    single { UserRepository(get<BudgetBuddyDatabase>().userDAO()) }
    single { CategoryRepository(get<BudgetBuddyDatabase>().categoryDAO()) }
    single { ThemeRepository(get()) }
    single { CurrencyRepository(get(), get()) }
    viewModel { TransactionViewModel(get()) }
    viewModel { RegularTransactionViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { CurrencyViewModel(get()) }
    viewModel { ThemeViewModel(get()) }
}