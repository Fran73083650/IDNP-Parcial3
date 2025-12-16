package com.example.parcial3idnp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parcial3idnp.data.local.ActivityEntity
import com.example.parcial3idnp.data.repository.ActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    private val _activities = MutableStateFlow<List<ActivityEntity>>(emptyList())
    val activities: StateFlow<List<ActivityEntity>> = _activities.asStateFlow()

    init {
        loadActivities()
    }

    private fun loadActivities() {
        viewModelScope.launch {
            repository.getAllPendingActivities().collect { activitiesList ->
                _activities.value = activitiesList
            }
        }
    }

    // NUEVA FUNCIÓN: Obtener actividad por ID
    suspend fun getActivityById(id: Int): ActivityEntity? {
        return repository.getActivityById(id)
    }

    fun addActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.insertActivity(activity)
        }
    }

    fun updateActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.updateActivity(activity)
        }
    }

    fun deleteActivity(activity: ActivityEntity) {
        viewModelScope.launch {
            repository.deleteActivity(activity)
        }
    }
}