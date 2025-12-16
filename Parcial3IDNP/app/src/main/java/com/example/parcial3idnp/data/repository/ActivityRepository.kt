package com.example.parcial3idnp.data.repository

import com.example.parcial3idnp.data.local.ActivityDao
import com.example.parcial3idnp.data.local.ActivityEntity
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val activityDao: ActivityDao) {

    fun getAllPendingActivities(): Flow<List<ActivityEntity>> {
        return activityDao.getAllPendingActivities()
    }

    suspend fun getActivityById(id: Int): ActivityEntity? {
        return activityDao.getActivityById(id)
    }

    suspend fun insertActivity(activity: ActivityEntity): Long {
        return activityDao.insertActivity(activity)
    }

    suspend fun updateActivity(activity: ActivityEntity) {
        activityDao.updateActivity(activity)
    }

    suspend fun deleteActivity(activity: ActivityEntity) {
        activityDao.deleteActivity(activity)
    }

    suspend fun deleteActivityById(id: Int) {
        activityDao.deleteActivityById(id)
    }
}