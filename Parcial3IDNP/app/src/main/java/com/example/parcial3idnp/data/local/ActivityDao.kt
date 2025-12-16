package com.example.parcial3idnp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activities WHERE isCompleted = 0 ORDER BY dueDate ASC")
    fun getAllPendingActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :activityId")
    suspend fun getActivityById(activityId: Int): ActivityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity): Long

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE id = :activityId")
    suspend fun deleteActivityById(activityId: Int)
}