package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CalisthenicsData
import com.example.ui.CalisthenicsViewModel
import com.example.ui.components.ExerciseDetailDialog
import com.example.ui.components.RecordEditDialog
import com.example.ui.components.WorkoutCompleteDialog
import com.example.ui.screens.ActiveWorkoutPlayer
import com.example.ui.screens.ExercisesScreen
import com.example.ui.screens.HistoryAndRecordsScreen
import com.example.ui.screens.PracticeTimerScreen
import com.example.ui.screens.RoutinesScreen
import com.example.ui.theme.CalisthenicsOrange
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VibrantNavBg
import com.example.ui.theme.VibrantOnPill
import com.example.ui.theme.VibrantPurplePill

enum class AppTab(val title: String, val icon: ImageVector) {
    ROUTINES("Workouts", Icons.Default.FitnessCenter),
    EXERCISES("Skills", Icons.Default.FormatListBulleted),
    TIMER("Timer", Icons.Default.Timer),
    HISTORY("Activity", Icons.Default.EmojiEvents)
}

class MainActivity : ComponentActivity() {
    private val viewModel: CalisthenicsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                CalisthenicsApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalisthenicsApp(viewModel: CalisthenicsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val workoutLogs by viewModel.workoutLogs.collectAsStateWithLifecycle()
    val personalRecords by viewModel.personalRecords.collectAsStateWithLifecycle()

    var currentTab by rememberSaveable { mutableIntStateOf(0) }

    // If a workout is actively running, show the dedicated player full screen
    val activeSession = uiState.activeSession
    if (activeSession != null) {
        ActiveWorkoutPlayer(
            session = activeSession,
            onToggleTimer = viewModel::toggleWorkoutTimer,
            onCompleteSet = viewModel::completeCurrentSet,
            onSkipRest = viewModel::skipRest,
            onAdjustRest = viewModel::adjustRestTimer,
            onCancelWorkout = viewModel::cancelActiveWorkout,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentTab) {
                                0 -> "Calisthenics"
                                1 -> "Movement Library"
                                2 -> "Practice Timer"
                                else -> "Workout History & PRs"
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = VibrantNavBg,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .testTag("bottom_nav_bar")
                ) {
                    AppTab.values().forEachIndexed { index, tab ->
                        val isSelected = currentTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { currentTab = index },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.title
                                )
                            },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VibrantOnPill,
                                indicatorColor = VibrantPurplePill,
                                selectedTextColor = VibrantOnPill,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "tabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        0 -> RoutinesScreen(
                            routines = CalisthenicsData.allRoutines,
                            selectedCategory = uiState.selectedCategory,
                            totalWorkoutsCompleted = workoutLogs.size,
                            onSelectCategory = viewModel::selectCategory,
                            onStartRoutine = viewModel::startRoutine,
                            onViewExerciseDetail = { exId ->
                                val ex = CalisthenicsData.getExerciseById(exId)
                                if (ex != null) viewModel.showExerciseDetail(ex)
                            }
                        )
                        1 -> ExercisesScreen(
                            exercises = CalisthenicsData.allExercises,
                            searchQuery = uiState.searchQuery,
                            selectedCategory = uiState.selectedCategory,
                            onSearchQueryChange = viewModel::updateSearchQuery,
                            onSelectCategory = viewModel::selectCategory,
                            onSelectExercise = viewModel::showExerciseDetail,
                            onLogPr = viewModel::openRecordEditor,
                            onPracticeTimer = { ex ->
                                viewModel.startPracticeTimer(ex.holdDurationSeconds)
                                currentTab = 2 // Switch to timer tab
                            }
                        )
                        2 -> PracticeTimerScreen(
                            secondsRemaining = uiState.practiceTimerSecondsRemaining,
                            initialSeconds = uiState.practiceTimerInitialSeconds,
                            isRunning = uiState.isPracticeTimerRunning,
                            onToggleTimer = viewModel::togglePracticeTimer,
                            onResetTimer = viewModel::resetPracticeTimer,
                            onStartTimerWithDuration = viewModel::startPracticeTimer,
                            onSaveBenchPr = viewModel::openRecordEditor
                        )
                        3 -> HistoryAndRecordsScreen(
                            workoutLogs = workoutLogs,
                            personalRecords = personalRecords,
                            onDeleteLog = viewModel::deleteLog,
                            onEditRecordForExercise = viewModel::openRecordEditor,
                            onStartWorkoutClick = { currentTab = 0 }
                        )
                    }
                }
            }
        }
    }

    // Exercise Detail Dialog
    val selectedEx = uiState.selectedExerciseDetail
    if (selectedEx != null) {
        ExerciseDetailDialog(
            exercise = selectedEx,
            onDismiss = { viewModel.showExerciseDetail(null) },
            onPracticeWithTimer = { ex ->
                viewModel.startPracticeTimer(ex.holdDurationSeconds)
                currentTab = 2
            }
        )
    }

    // Workout Complete Dialog
    val completedLog = uiState.workoutCompletedLog
    if (completedLog != null) {
        WorkoutCompleteDialog(
            completedLog = completedLog,
            onSave = { exertion, notes ->
                viewModel.saveCompletedWorkout(exertion, notes)
                currentTab = 3 // Jump to History tab so user sees their new workout & stats!
            },
            onDismiss = viewModel::dismissCompletedSummary
        )
    }

    // Record Editor Dialog
    val editingRecordEx = uiState.showRecordEditDialogFor
    if (editingRecordEx != null) {
        RecordEditDialog(
            exercise = editingRecordEx,
            onDismiss = { viewModel.openRecordEditor(null) },
            onSave = { exId, exName, value, unit ->
                viewModel.savePersonalRecord(exId, exName, value, unit)
            }
        )
    }
}
