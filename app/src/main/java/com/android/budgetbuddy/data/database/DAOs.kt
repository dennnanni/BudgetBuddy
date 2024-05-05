package com.android.budgetbuddy.data.database


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDAO {
    @Query("SELECT * FROM `transaction`")
    fun getAll(): Flow<List<Transaction>>

    @Query(
        "SELECT category\n" +
                "FROM `transaction`\n" +
                "GROUP BY category\n" +
                "ORDER BY SUM(amount) DESC\n" +
                "LIMIT 2"
    )
    suspend fun getMostPopularCategories(): List<String>

    @Upsert
    suspend fun upsert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}

@Dao
interface UserDAO {
    @Query("SELECT * FROM `user`")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM `user` WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User

    @Upsert
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(user: User)
}