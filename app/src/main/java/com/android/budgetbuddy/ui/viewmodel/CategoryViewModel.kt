package com.android.budgetbuddy.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.data.database.Category
import com.android.budgetbuddy.data.repositories.CategoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

interface CategoryActions {

    fun loadCategories(userId: Int): Job

    fun getCategories(): List<Category>
    fun addCategory(category: Category, userId: Int): Job
    fun removeCategory(category: Category): Job
}

class CategoryViewModel(private val repository: CategoryRepository) : ViewModel() {
    var categories = mutableStateOf<List<Category>>(emptyList())

    val actions = object : CategoryActions {
        override fun loadCategories(userId: Int): Job {
            return viewModelScope.launch {
                categories.value = repository.getAll(userId)
            }
        }

        override fun addCategory(category: Category, userId: Int): Job {
            return viewModelScope.launch {
                viewModelScope.launch {
                    repository.upsert(category)
                }.join()
                loadCategories(userId)
            }
        }

        override fun removeCategory(category: Category): Job {
            return viewModelScope.launch {
                repository.delete(category)
            }
        }

        override fun getCategories(): List<Category> {
            return categories.value
        }
    }
}