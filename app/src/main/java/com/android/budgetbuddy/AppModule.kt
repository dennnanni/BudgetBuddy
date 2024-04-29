package com.android.budgetbuddy

import androidx.room.Room
import com.android.budgetbuddy.data.database.BudgetBuddyDatabase
import com.android.budgetbuddy.data.repositories.TransactionRepository
import com.android.budgetbuddy.ui.TransactionViewModel
import com.android.budgetbuddy.ui.viewmodel.UserViewModel
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val appModule = module {
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
    viewModel { TransactionViewModel(get()) }
    viewModel { UserViewModel(get()) }
}