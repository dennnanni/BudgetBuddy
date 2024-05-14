package com.android.budgetbuddy.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.data.database.EarnedBadge
import com.android.budgetbuddy.data.repositories.EarnedBadgeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface EarnedBadgeActions {
    fun loadEarnedBadges(userId: Int) : Job
    fun addEarnedBadge(earnedBadge: EarnedBadge): Job
    fun removeEarnedBadge(earnedBadge: EarnedBadge): Job
}

class EarnedBadgeViewModel(private val repository: EarnedBadgeRepository) : ViewModel() {
    val earnedBadges = mutableStateOf<List<EarnedBadge>>(emptyList())

    val actions = object : EarnedBadgeActions {
        override fun loadEarnedBadges(userId: Int): Job {
            return viewModelScope.launch {
                earnedBadges.value = repository.getUserBadges(userId)
            }
        }

        override fun addEarnedBadge(earnedBadge: EarnedBadge): Job {
            return viewModelScope.launch {
                viewModelScope.launch {
                    repository.upsert(earnedBadge)
                }.join()
                loadEarnedBadges(earnedBadge.userId)
            }
        }

        override fun removeEarnedBadge(earnedBadge: EarnedBadge): Job {
            return viewModelScope.launch {
                repository.delete(earnedBadge)
            }
        }
    }
}