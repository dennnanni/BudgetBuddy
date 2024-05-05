package com.android.budgetbuddy.data.database


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(tableName = "transaction")
data class Transaction (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val title: String,
    @ColumnInfo val description: String,
    @ColumnInfo val type: String,
    @ColumnInfo val category: String,
    @ColumnInfo val amount: Double,
    @ColumnInfo val date: Date,
    @ColumnInfo val periodic: Boolean
)

@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val username: String,
    @ColumnInfo val password: String,
    @ColumnInfo(defaultValue = "temp") var profilePic: String? = ""
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