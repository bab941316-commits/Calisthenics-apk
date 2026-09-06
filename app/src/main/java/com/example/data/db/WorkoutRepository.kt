package com.example.data.db

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allLogs: Flow<List<WorkoutLog>> = workoutDao.getAllWorkoutLogs()
    val allRecords: Flow<List<PersonalRecord>> = workoutDao.getAllPersonalRecords()
    val totalWorkoutsCount: Flow<Int> = workoutDao.getTotalWorkoutsCount()

    suspend fun saveWorkout(log: WorkoutLog): Long {
        return workoutDao.insertWorkoutLog(log)
    }

    suspend fun deleteWorkout(id: Long) {
        workoutDao.deleteWorkoutLog(id)
    }

    suspend fun saveRecord(record: PersonalRecord) {
        workoutDao.upsertPersonalRecord(record)
    }

    suspend fun seedInitialRecordsIfEmpty(currentRecords: List<PersonalRecord>) {
        if (currentRecords.isEmpty()) {
            val defaults = listOf(
                PersonalRecord("standard_pushups", "Standard Push-ups", "15 reps", 15, "reps"),
                PersonalRecord("standard_pullups", "Strict Pull-ups", "5 reps", 5, "reps"),
                PersonalRecord("parallel_dips", "Parallel Bar Dips", "8 reps", 8, "reps"),
                PersonalRecord("plank_hold", "Plank Hold", "60 sec", 60, "sec"),
                PersonalRecord("l_sit_hold", "L-Sit Hold", "12 sec", 12, "sec")
            )
            for (rec in defaults) {
                workoutDao.upsertPersonalRecord(rec)
            }
        }
    }

    suspend fun seedInitialLogsIfEmpty(currentLogs: List<WorkoutLog>) {
        if (currentLogs.isEmpty()) {
            val now = System.currentTimeMillis()
            val oneDayMillis = 24 * 60 * 60 * 1000L
            val sampleLogs = listOf(
                WorkoutLog(
                    routineId = "foundation_upper_body",
                    routineTitle = "Upper Body Foundation",
                    timestamp = now - (0 * oneDayMillis), // Today
                    durationSeconds = 1140, // 19 min
                    exercisesCompleted = 4,
                    totalExercises = 4,
                    perceivedExertion = 4,
                    notes = "Clean push-up form and strict dips lockout."
                ),
                WorkoutLog(
                    routineId = "core_compression",
                    routineTitle = "Core Compression & Hollow Body",
                    timestamp = now - (1 * oneDayMillis), // Yesterday
                    durationSeconds = 870, // 14.5 min
                    exercisesCompleted = 4,
                    totalExercises = 4,
                    perceivedExertion = 5,
                    notes = "L-sit progression hold felt solid."
                ),
                WorkoutLog(
                    routineId = "handstand_skill",
                    routineTitle = "Handstand Balance & Shoulder Prep",
                    timestamp = now - (3 * oneDayMillis), // 3 days ago
                    durationSeconds = 1200, // 20 min
                    exercisesCompleted = 5,
                    totalExercises = 5,
                    perceivedExertion = 3,
                    notes = "Consistent chest-to-wall balance holds."
                ),
                WorkoutLog(
                    routineId = "explosive_pulling",
                    routineTitle = "Explosive Pulling & Muscle-up",
                    timestamp = now - (5 * oneDayMillis), // 5 days ago
                    durationSeconds = 1380, // 23 min
                    exercisesCompleted = 4,
                    totalExercises = 4,
                    perceivedExertion = 4,
                    notes = "High pull-ups touching sternum!"
                ),
                WorkoutLog(
                    routineId = "foundation_upper_body",
                    routineTitle = "Upper Body Foundation",
                    timestamp = now - (8 * oneDayMillis), // 8 days ago
                    durationSeconds = 1050, // 17.5 min
                    exercisesCompleted = 4,
                    totalExercises = 4,
                    perceivedExertion = 4,
                    notes = "Great tempo and shoulder depression."
                )
            )
            for (log in sampleLogs) {
                workoutDao.insertWorkoutLog(log)
            }
        }
    }
}
