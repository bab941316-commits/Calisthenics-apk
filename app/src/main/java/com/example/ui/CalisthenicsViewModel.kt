package com.example.ui

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.PersonalRecord
import com.example.data.db.WorkoutLog
import com.example.data.db.WorkoutRepository
import com.example.data.model.CalisthenicsData
import com.example.data.model.DifficultyLevel
import com.example.data.model.Exercise
import com.example.data.model.MovementCategory
import com.example.data.model.WorkoutRoutine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class WorkoutTimerMode {
    WORK,
    REST,
    COMPLETED
}

data class ActiveWorkoutSession(
    val routine: WorkoutRoutine,
    val currentExerciseIndex: Int = 0,
    val currentSet: Int = 1,
    val timerMode: WorkoutTimerMode = WorkoutTimerMode.WORK,
    val timerSecondsRemaining: Int = 0,
    val initialTimerDuration: Int = 0,
    val isTimerRunning: Boolean = false,
    val totalElapsedSeconds: Int = 0,
    val completedExercisesCount: Int = 0
)

data class WorkoutUiState(
    val selectedCategory: MovementCategory? = null,
    val searchQuery: String = "",
    val selectedExerciseDetail: Exercise? = null,
    val activeSession: ActiveWorkoutSession? = null,
    val workoutCompletedLog: WorkoutLog? = null,
    val showRecordEditDialogFor: Exercise? = null,
    // Standalone Practice Timer
    val practiceTimerSecondsRemaining: Int = 30,
    val practiceTimerInitialSeconds: Int = 30,
    val isPracticeTimerRunning: Boolean = false
)

class CalisthenicsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorkoutRepository
    private var workoutTimerJob: Job? = null
    private var practiceTimerJob: Job? = null
    private var toneGenerator: ToneGenerator? = null

    init {
        val db = AppDatabase.getDatabase(application)
        repository = WorkoutRepository(db.workoutDao())

        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) {
            toneGenerator = null
        }

        viewModelScope.launch {
            repository.allRecords.collect { records ->
                if (records.isEmpty()) {
                    repository.seedInitialRecordsIfEmpty(records)
                }
            }
        }

        viewModelScope.launch {
            repository.allLogs.collect { logs ->
                if (logs.isEmpty()) {
                    repository.seedInitialLogsIfEmpty(logs)
                }
            }
        }
    }

    private val _uiState = MutableStateFlow(WorkoutUiState())
    val uiState: StateFlow<WorkoutUiState> = _uiState.asStateFlow()

    val workoutLogs: StateFlow<List<WorkoutLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val personalRecords: StateFlow<List<PersonalRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filter & Search
    fun selectCategory(category: MovementCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun showExerciseDetail(exercise: Exercise?) {
        _uiState.update { it.copy(selectedExerciseDetail = exercise) }
    }

    fun openRecordEditor(exercise: Exercise?) {
        _uiState.update { it.copy(showRecordEditDialogFor = exercise) }
    }

    fun savePersonalRecord(exerciseId: String, exerciseName: String, numericValue: Int, unit: String) {
        viewModelScope.launch {
            val recordStr = "$numericValue $unit"
            repository.saveRecord(
                PersonalRecord(
                    exerciseId = exerciseId,
                    exerciseName = exerciseName,
                    recordValue = recordStr,
                    recordNumeric = numericValue,
                    unit = unit
                )
            )
            _uiState.update { it.copy(showRecordEditDialogFor = null) }
        }
    }

    // Workout Player controls
    fun startRoutine(routine: WorkoutRoutine) {
        val firstExercise = routine.exercises.firstOrNull() ?: return
        val exDetail = CalisthenicsData.getExerciseById(firstExercise.exerciseId)
        val initialDuration = if (firstExercise.isTimed) firstExercise.durationSeconds else (exDetail?.holdDurationSeconds ?: 30)

        val session = ActiveWorkoutSession(
            routine = routine,
            currentExerciseIndex = 0,
            currentSet = 1,
            timerMode = WorkoutTimerMode.WORK,
            timerSecondsRemaining = initialDuration,
            initialTimerDuration = initialDuration,
            isTimerRunning = firstExercise.isTimed,
            totalElapsedSeconds = 0,
            completedExercisesCount = 0
        )
        _uiState.update { it.copy(activeSession = session, workoutCompletedLog = null) }
        startWorkoutTicker()
    }

    fun toggleWorkoutTimer() {
        _uiState.update { state ->
            val session = state.activeSession ?: return@update state
            session.copy(isTimerRunning = !session.isTimerRunning).let { updated ->
                state.copy(activeSession = updated)
            }
        }
    }

    fun completeCurrentSet() {
        val session = _uiState.value.activeSession ?: return
        val currentRoutineExercise = session.routine.exercises[session.currentExerciseIndex]

        playBeepTone(isHighPitch = false)

        if (session.currentSet < currentRoutineExercise.sets) {
            // Enter Rest Mode for next set
            val restTime = currentRoutineExercise.restSeconds
            _uiState.update { state ->
                state.copy(
                    activeSession = session.copy(
                        currentSet = session.currentSet + 1,
                        timerMode = WorkoutTimerMode.REST,
                        timerSecondsRemaining = restTime,
                        initialTimerDuration = restTime,
                        isTimerRunning = true
                    )
                )
            }
        } else {
            // Exercise completed, advance to next exercise or finish routine
            val nextExerciseIndex = session.currentExerciseIndex + 1
            if (nextExerciseIndex < session.routine.exercises.size) {
                val nextRoutineExercise = session.routine.exercises[nextExerciseIndex]
                val nextDetail = CalisthenicsData.getExerciseById(nextRoutineExercise.exerciseId)
                val nextDuration = if (nextRoutineExercise.isTimed) nextRoutineExercise.durationSeconds else (nextDetail?.holdDurationSeconds ?: 30)

                // Give rest between exercises (60s default)
                _uiState.update { state ->
                    state.copy(
                        activeSession = session.copy(
                            currentExerciseIndex = nextExerciseIndex,
                            currentSet = 1,
                            timerMode = WorkoutTimerMode.REST,
                            timerSecondsRemaining = 60,
                            initialTimerDuration = 60,
                            isTimerRunning = true,
                            completedExercisesCount = session.completedExercisesCount + 1
                        )
                    )
                }
            } else {
                // Workout Completed!
                finishWorkoutSession(session)
            }
        }
    }

    fun skipRest() {
        val session = _uiState.value.activeSession ?: return
        if (session.timerMode == WorkoutTimerMode.REST) {
            val curRoutineEx = session.routine.exercises[session.currentExerciseIndex]
            val curDetail = CalisthenicsData.getExerciseById(curRoutineEx.exerciseId)
            val duration = if (curRoutineEx.isTimed) curRoutineEx.durationSeconds else (curDetail?.holdDurationSeconds ?: 30)

            _uiState.update { state ->
                state.copy(
                    activeSession = session.copy(
                        timerMode = WorkoutTimerMode.WORK,
                        timerSecondsRemaining = duration,
                        initialTimerDuration = duration,
                        isTimerRunning = curRoutineEx.isTimed
                    )
                )
            }
        }
    }

    fun adjustRestTimer(deltaSeconds: Int) {
        _uiState.update { state ->
            val session = state.activeSession ?: return@update state
            val newTime = (session.timerSecondsRemaining + deltaSeconds).coerceAtLeast(5)
            state.copy(
                activeSession = session.copy(
                    timerSecondsRemaining = newTime,
                    initialTimerDuration = maxOf(session.initialTimerDuration, newTime)
                )
            )
        }
    }

    fun cancelActiveWorkout() {
        workoutTimerJob?.cancel()
        workoutTimerJob = null
        _uiState.update { it.copy(activeSession = null, workoutCompletedLog = null) }
    }

    private fun finishWorkoutSession(session: ActiveWorkoutSession) {
        workoutTimerJob?.cancel()
        workoutTimerJob = null
        playVictoryBeep()

        val completedLog = WorkoutLog(
            routineId = session.routine.id,
            routineTitle = session.routine.title,
            durationSeconds = session.totalElapsedSeconds,
            exercisesCompleted = session.routine.exercises.size,
            totalExercises = session.routine.exercises.size,
            perceivedExertion = 3,
            notes = "Completed all sets!"
        )

        _uiState.update {
            it.copy(
                activeSession = null,
                workoutCompletedLog = completedLog
            )
        }
    }

    fun saveCompletedWorkout(perceivedExertion: Int, notes: String) {
        val log = _uiState.value.workoutCompletedLog ?: return
        viewModelScope.launch {
            repository.saveWorkout(
                log.copy(
                    perceivedExertion = perceivedExertion,
                    notes = notes
                )
            )
            _uiState.update { it.copy(workoutCompletedLog = null) }
        }
    }

    fun dismissCompletedSummary() {
        _uiState.update { it.copy(workoutCompletedLog = null) }
    }

    fun deleteLog(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkout(id)
        }
    }

    // Standalone Practice Timer controls
    fun startPracticeTimer(durationSeconds: Int) {
        practiceTimerJob?.cancel()
        _uiState.update {
            it.copy(
                practiceTimerSecondsRemaining = durationSeconds,
                practiceTimerInitialSeconds = durationSeconds,
                isPracticeTimerRunning = true
            )
        }
        practiceTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val current = _uiState.value
                if (!current.isPracticeTimerRunning) continue

                val rem = current.practiceTimerSecondsRemaining - 1
                if (rem <= 0) {
                    playBeepTone(isHighPitch = true)
                    _uiState.update {
                        it.copy(
                            practiceTimerSecondsRemaining = 0,
                            isPracticeTimerRunning = false
                        )
                    }
                    break
                } else {
                    if (rem in 1..3) playTick()
                    _uiState.update { it.copy(practiceTimerSecondsRemaining = rem) }
                }
            }
        }
    }

    fun togglePracticeTimer() {
        val current = _uiState.value
        if (current.isPracticeTimerRunning) {
            _uiState.update { it.copy(isPracticeTimerRunning = false) }
        } else {
            if (current.practiceTimerSecondsRemaining <= 0) {
                startPracticeTimer(current.practiceTimerInitialSeconds)
            } else {
                _uiState.update { it.copy(isPracticeTimerRunning = true) }
            }
        }
    }

    fun resetPracticeTimer(seconds: Int = 30) {
        practiceTimerJob?.cancel()
        _uiState.update {
            it.copy(
                practiceTimerSecondsRemaining = seconds,
                practiceTimerInitialSeconds = seconds,
                isPracticeTimerRunning = false
            )
        }
    }

    private fun startWorkoutTicker() {
        workoutTimerJob?.cancel()
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val currentSession = _uiState.value.activeSession ?: break

                val newElapsed = currentSession.totalElapsedSeconds + 1

                if (currentSession.isTimerRunning) {
                    val newRemaining = currentSession.timerSecondsRemaining - 1

                    if (newRemaining <= 0) {
                        // Timer expired
                        playBeepTone(isHighPitch = true)

                        if (currentSession.timerMode == WorkoutTimerMode.REST) {
                            // Rest ended -> ready for next set
                            val curRoutineEx = currentSession.routine.exercises[currentSession.currentExerciseIndex]
                            val curDetail = CalisthenicsData.getExerciseById(curRoutineEx.exerciseId)
                            val duration = if (curRoutineEx.isTimed) curRoutineEx.durationSeconds else (curDetail?.holdDurationSeconds ?: 30)

                            _uiState.update { state ->
                                state.copy(
                                    activeSession = currentSession.copy(
                                        totalElapsedSeconds = newElapsed,
                                        timerMode = WorkoutTimerMode.WORK,
                                        timerSecondsRemaining = duration,
                                        initialTimerDuration = duration,
                                        isTimerRunning = curRoutineEx.isTimed
                                    )
                                )
                            }
                        } else {
                            // Work timer expired for timed exercise
                            _uiState.update { state ->
                                state.copy(
                                    activeSession = currentSession.copy(
                                        totalElapsedSeconds = newElapsed,
                                        timerSecondsRemaining = 0,
                                        isTimerRunning = false
                                    )
                                )
                            }
                        }
                    } else {
                        if (newRemaining in 1..3) playTick()
                        _uiState.update { state ->
                            state.copy(
                                activeSession = currentSession.copy(
                                    totalElapsedSeconds = newElapsed,
                                    timerSecondsRemaining = newRemaining
                                )
                            )
                        }
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            activeSession = currentSession.copy(
                                totalElapsedSeconds = newElapsed
                            )
                        )
                    }
                }
            }
        }
    }

    private fun playBeepTone(isHighPitch: Boolean) {
        try {
            val tone = if (isHighPitch) ToneGenerator.TONE_PROP_BEEP2 else ToneGenerator.TONE_PROP_BEEP
            toneGenerator?.startTone(tone, 200)
        } catch (_: Exception) {}
    }

    private fun playTick() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_PIP, 80)
        } catch (_: Exception) {}
    }

    private fun playVictoryBeep() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 400)
        } catch (_: Exception) {}
    }

    override fun onCleared() {
        super.onCleared()
        workoutTimerJob?.cancel()
        practiceTimerJob?.cancel()
        try {
            toneGenerator?.release()
        } catch (_: Exception) {}
    }
}
