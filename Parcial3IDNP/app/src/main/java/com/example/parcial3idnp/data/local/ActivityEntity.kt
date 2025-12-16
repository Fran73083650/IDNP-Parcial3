package com.example.parcial3idnp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "activities")
@TypeConverters(Converters::class)
data class ActivityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: String,
    val dueTime: String?,
    val category: String,
    val reminders: List<ReminderItem> = emptyList(), // Lista de recordatorios
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

// Modelo para cada recordatorio
data class ReminderItem(
    val amount: Int,
    val unit: ReminderUnit
)

enum class ReminderUnit {
    MINUTES, HOURS, DAYS;

    fun toMinutes(amount: Int): Int {
        return when (this) {
            MINUTES -> amount
            HOURS -> amount * 60
            DAYS -> amount * 1440
        }
    }

    fun displayName(): String {
        return when (this) {
            MINUTES -> "minutos"
            HOURS -> "horas"
            DAYS -> "días"
        }
    }
}

// Converters para Room Database
class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromReminderList(value: List<ReminderItem>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toReminderList(value: String): List<ReminderItem> {
        val listType = object : TypeToken<List<ReminderItem>>() {}.type
        return gson.fromJson(value, listType)
    }
}