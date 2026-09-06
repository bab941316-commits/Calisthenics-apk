package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLog): Long

    @Query("DELETE FROM workout_logs WHERE id = :id")
    suspend fun deleteWorkoutLog(id: Long)

    @Query("SELECT * FROM personal_records ORDER BY exerciseName ASC")
    fun getAllPersonalRecords(): Flow<List<PersonalRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPersonalRecord(record: PersonalRecord)

    @Query("SELECT COUNT(*) FROM workout_logs")
    fun getTotalWorkoutsCount(): Flow<Int>
}
