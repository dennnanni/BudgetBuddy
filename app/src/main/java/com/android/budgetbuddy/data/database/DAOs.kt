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

    @Query("SELECT * FROM `transaction` WHERE userId = :userId")
    suspend fun getUserTransactions(userId: Int): List<Transaction>

    @Query(
        "SELECT category\n" +
                "FROM `transaction`\n" +
                "WHERE userId = :userId\n" +
                "AND type = \"Expense\"" +
                "GROUP BY category\n" +
                "ORDER BY SUM(amount) DESC\n" +
                "LIMIT 2"
    )
    suspend fun getMostPopularCategories(userId: Int): List<String>

    @Upsert
    suspend fun upsert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM `transaction`")
    suspend fun nukeTable()
}

@Dao
interface UserDAO {
    @Query("SELECT * FROM `user`")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM `user` WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User

    @Query("SELECT id FROM `user` WHERE username = :username LIMIT 1")
    suspend fun getUserId(username: String): Int

    @Query("SELECT * FROM `user` WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Upsert
    suspend fun upsert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM `user` WHERE username = :username LIMIT 1")
    suspend fun getUser(username: String): User
}

@Dao
interface CategoryDAO {
    @Query("SELECT * FROM `category` where userId = :userId")
    suspend fun getAll(userId: Int): List<Category>

    @Query("DELETE FROM `category` WHERE name = :name AND userId = :userId")
    suspend fun deleteCategory(name: String, userId: Int)

    @Upsert
    suspend fun upsert(category: Category)

    @Delete
    suspend fun delete(category: Category)
}

@Dao
interface RegularTransactionDAO {
    @Query("SELECT * FROM `regular_transaction`")
    fun getAll(): Flow<List<RegularTransactions>>

    @Query("SELECT * FROM `regular_transaction` WHERE userId = :userId")
    suspend fun getUserTransactions(userId: Int): List<RegularTransactions>

    @Query(
        "SELECT category\n" +
                "FROM `regular_transaction`\n" +
                "GROUP BY category\n" +
                "ORDER BY SUM(amount) DESC\n" +
                "LIMIT 2"
    )
    suspend fun getMostPopularCategories(): List<String>

    @Upsert
    suspend fun upsert(transaction: RegularTransactions)

    @Delete
    suspend fun delete(transaction: RegularTransactions)

    @Query("DELETE FROM `regular_transaction`")
    suspend fun nukeTable()
}

@Dao
interface EarnedBadgeDAO {

    @Query("SELECT * FROM `earned_badge` WHERE userId = :userId")
    suspend fun getUserBadges(userId: Int): List<EarnedBadge>

    @Query("SELECT * FROM `earned_badge` WHERE userId = :userId ORDER BY id DESC LIMIT 1")
    suspend fun getLastEarnedBadge(userId: Int): EarnedBadge

    @Upsert
    suspend fun upsert(earnedBadge: EarnedBadge)

    @Delete
    suspend fun delete(earnedBadge: EarnedBadge)
}