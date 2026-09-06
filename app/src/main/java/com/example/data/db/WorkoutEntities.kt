package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val routineId: String,
    val routineTitle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val durationSeconds: Int,
    val exercisesCompleted: Int,
    val totalExercises: Int,
    val perceivedExertion: Int = 3, // 1 to 5 scale
    val notes: String = ""
)

@Entity(tableName = "personal_records")
data class PersonalRecord(
    @PrimaryKey
    val exerciseId: String,
    val exerciseName: String,
    val recordValue: String,
    val recordNumeric: Int, // e.g. 20 (reps) or 45 (seconds)
    val unit: String, // "reps" or "sec"
    val timestamp: Long = System.currentTimeMillis()
)
