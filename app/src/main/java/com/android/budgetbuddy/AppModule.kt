package com.android.budgetbuddy

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.android.budgetbuddy.data.database.BudgetBuddyDatabase
import com.android.budgetbuddy.data.repositories.CategoryRepository
import com.android.budgetbuddy.data.repositories.ThemeRepository
import com.android.budgetbuddy.data.repositories.TransactionRepository
import com.android.budgetbuddy.data.repositories.UserRepository
import com.android.budgetbuddy.ui.viewmodel.TransactionViewModel
import com.android.budgetbuddy.ui.screens.settings.ThemeViewModel
import com.android.budgetbuddy.ui.viewmodel.CategoryViewModel
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

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

    single { TransactionRepository(get<BudgetBuddyDatabase>().transactionDAO()) }
    single { UserRepository(get<BudgetBuddyDatabase>().userDao()) }
    single { CategoryRepository(get<BudgetBuddyDatabase>().categoryDAO()) }
    single { ThemeRepository(get()) }
    viewModel { TransactionViewModel(get()) }
    viewModel { UserViewModel(get()) }
    viewModel { CategoryViewModel(get()) }
    viewModel { ThemeViewModel(get()) }
}