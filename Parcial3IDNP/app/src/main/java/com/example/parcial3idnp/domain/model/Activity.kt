package com.example.parcial3idnp.domain.model

import java.time.LocalDate

data class Activity(
    val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: LocalDate,
    val category: ActivityCategory,
    val reminderDaysBefore: Int = 0,
    val isCompleted: Boolean = false
)

enum class ActivityCategory(val displayName: String) {
    UNIVERSIDAD("Universidad"),
    CASA("Casa"),
    TRABAJO("Trabajo"),
    OTROS("Otros")
}