package com.android.budgetbuddy.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.budgetbuddy.data.database.User
import com.android.budgetbuddy.data.repositories.UserRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class UserState(val users: List<User>)

interface UserActions {
    fun addUser(user: User): Job
    fun removeUser(user: User): Job
    fun login(username: String, password: String): Job

    fun getLoggedUser(): User?
}

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    var user = mutableStateOf<User?>(null)
        private set
    val state = repository.users.map { UserState(it) }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = UserState(emptyList())
    )

    val actions = object : UserActions {
        override fun addUser(user: User) = viewModelScope.launch {
            repository.upsert(user)
        }

        override fun removeUser(user: User) = viewModelScope.launch {
            repository.delete(user)
        }

        override fun login(username: String, password: String): Job = viewModelScope.launch {
            user.value = repository.login(username, password)
        }

        override fun getLoggedUser(): User? {
            Log.d("UserViewModel", "getLoggedUser: ${user.value}")
            return user.value
        }
    }
}