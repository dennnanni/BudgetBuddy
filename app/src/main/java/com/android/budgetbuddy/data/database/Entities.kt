package com.android.budgetbuddy.data.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "transaction",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class Transaction (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo var title: String,
    @ColumnInfo var description: String,
    @ColumnInfo var type: String,
    @ColumnInfo var category: String,
    @ColumnInfo var amount: Double,
    @ColumnInfo var date: Date,
    @ColumnInfo val periodic: Boolean,
    @ColumnInfo val userId: Int,
    @ColumnInfo var latitude: Double = 0.0,
    @ColumnInfo var longitude: Double = 0.0
) {
    val isExpense: Boolean
        get() {
            return type == "Expense"
        }
}

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val username: String,
    @ColumnInfo val password: String,
    @ColumnInfo(defaultValue = "temp") var profilePic: String? = ""
)

@Entity(tableName = "category", foreignKeys = [
    ForeignKey(
        entity = User::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val icon: String,
    @ColumnInfo val userId: Int,

)

@Entity(tableName = "regular_transaction",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class RegularTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val title: String,
    @ColumnInfo val description: String,
    @ColumnInfo val type: String,
    @ColumnInfo val category: String,
    @ColumnInfo val amount: Double,
    @ColumnInfo val interval: Long,
    @ColumnInfo val userId: Int,
    @ColumnInfo var lastUpdate: Date
) {
    val isExpense: Boolean
        get() {
            return type == "Expense"
        }
}

@Entity(tableName = "earned_badge",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class EarnedBadge(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val userId: Int,
    @ColumnInfo val badgeName: String,
)

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}