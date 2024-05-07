package com.android.budgetbuddy.ui.screens.settings

import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.R
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
    JPY
}

data class CurrencyState(val currency: Currency, val rate: Double)

class CurrencyViewModel(

) : ViewModel()
{
    fun changeCurrency(currency: CurrencyState) =
        viewModelScope.launch {
            // TODO: si può fare la chiamata http qui?
            //repository.setCurrency(currency)
        }
}