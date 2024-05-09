package com.android.budgetbuddy.ui.screens.settings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.R
import com.android.budgetbuddy.data.repositories.CurrencyRepository
import com.android.budgetbuddy.data.repositories.ThemeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Theme {
    System,
    Light,
    Dark
}

data class ThemeState(val theme: Theme)
class ThemeViewModel(
    private val repository: ThemeRepository
) : ViewModel()
{
    val state = repository.theme.map { ThemeState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ThemeState(Theme.System)
    )
    fun changeTheme(theme: Theme) =
        viewModelScope.launch {
            repository.setTheme(theme)
        }
}

enum class Currency {
    USD,
    EUR,
    GBP,
    JPY;

    fun getSymbol() = when(this) {
        USD -> "$"
        EUR -> "€"
        GBP -> "£"
        JPY -> "¥"
    }
}

data class CurrencyState(val currency: Currency)

class CurrencyViewModel(
    private val repository: CurrencyRepository
) : ViewModel()
{
    private val currency = mutableStateOf(Currency.USD)

    private var rate by mutableDoubleStateOf(1.0)

    fun getCurrency(): Currency {
        viewModelScope.launch {
            currency.value = repository.getCurrency()
        }
        return currency.value
    }

    fun changeCurrency(currency: Currency) =
        viewModelScope.launch {
            repository.setCurrency(currency)
        }

    fun updateRate() = viewModelScope.launch {
        rate = repository.getUpdatedRate() ?: rate
    }

    fun convert(amount: Double): Double {
        Log.d("Pippo", "Convertito")
        return  amount * rate
    }
}