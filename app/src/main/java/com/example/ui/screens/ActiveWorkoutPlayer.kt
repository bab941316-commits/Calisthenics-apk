package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalisthenicsData
import com.example.ui.ActiveWorkoutSession
import com.example.ui.WorkoutTimerMode
import com.example.ui.components.CircularTimerDisplay
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.MuscleTagChip
import com.example.ui.components.VideoDemonstrationDialog
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantCyanBright
import com.example.ui.theme.VibrantCyanContainer
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurpleLight
import com.example.ui.theme.VibrantTrack

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveWorkoutPlayer(
    session: ActiveWorkoutSession,
    onToggleTimer: () -> Unit,
    onCompleteSet: () -> Unit,
    onSkipRest: () -> Unit,
    onAdjustRest: (Int) -> Unit,
    onCancelWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showCancelDialog by remember { mutableStateOf(false) }
    var showVideoDemoDialog by remember { mutableStateOf(false) }

    val currentRoutineExercise = session.routine.exercises[session.currentExerciseIndex]
    val exerciseDetail = CalisthenicsData.getExerciseById(currentRoutineExercise.exerciseId)

    if (showVideoDemoDialog && exerciseDetail != null) {
        VideoDemonstrationDialog(
            exercise = exerciseDetail,
            onDismiss = { showVideoDemoDialog = false }
        )
    }

    val elapsedMinutes = session.totalElapsedSeconds / 60
    val elapsedSeconds = session.totalElapsedSeconds % 60
    val formattedElapsed = String.format("%02d:%02d", elapsedMinutes, elapsedSeconds)

    val overallProgress = (session.currentExerciseIndex.toFloat() + (session.currentSet - 1f) / currentRoutineExercise.sets) / session.routine.exercises.size.toFloat()

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Quit Workout?") },
            text = { Text("Progress for this session will not be saved.") },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        onCancelWorkout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("btn_confirm_quit_workout")
                ) {
                    Text("Quit")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false },
                    modifier = Modifier.testTag("btn_keep_going_workout")
                ) {
                    Text("Keep Going")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("active_workout_player")
    ) {
        // Top Bar: Routine Name, Total Elapsed Time, Quit Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = session.routine.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Elapsed: $formattedElapsed",
                    fontSize = 13.sp,
                    color = VibrantPurple,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = { showCancelDialog = true },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("btn_quit_workout")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Exit Workout",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Progress bar with Vibrant Palette
        LinearProgressIndicator(
            progress = { overallProgress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = VibrantPurple,
            trackColor = VibrantPurpleLight
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Phase Banner (REST vs WORK)
        val isResting = session.timerMode == WorkoutTimerMode.REST
        Surface(
            color = if (isResting) VibrantCyanContainer else VibrantPurpleContainer,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = if (isResting) VibrantCyan else VibrantPurpleDark,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isResting) "REST & RECOVERY" else "ACTIVE SET",
                        color = if (isResting) VibrantCyan else VibrantPurpleDark,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "Exercise ${session.currentExerciseIndex + 1} of ${session.routine.exercises.size}",
                    color = if (isResting) VibrantCyan.copy(alpha = 0.85f) else VibrantPurpleDark.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scrollable content area
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isResting) {
                // Rest Mode View
                Spacer(modifier = Modifier.height(16.dp))
                CircularTimerDisplay(
                    secondsRemaining = session.timerSecondsRemaining,
                    totalSeconds = session.initialTimerDuration,
                    size = 180.dp,
                    progressColor = VibrantCyanBright,
                    trackColor = VibrantTrack,
                    label = "REST TIME"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Rest adjustments (-15s, +15s)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { onAdjustRest(-15) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("btn_rest_minus_15")
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("-15s", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onAdjustRest(15) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.testTag("btn_rest_plus_15")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+15s", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Up next card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "UP NEXT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPurple,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = exerciseDetail?.name ?: "Next Exercise",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Set ${session.currentSet} of ${currentRoutineExercise.sets} • ${currentRoutineExercise.targetRepsOrSeconds}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Active Work Mode View
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = VibrantPurpleContainer,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "SET ${session.currentSet} / ${currentRoutineExercise.sets}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp,
                                    color = VibrantPurpleDark,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            if (exerciseDetail != null) {
                                DifficultyBadge(difficulty = exerciseDetail.difficulty)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = exerciseDetail?.name ?: "Exercise",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Target: ${currentRoutineExercise.targetRepsOrSeconds}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPurple
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (currentRoutineExercise.isTimed) {
                            // Circular Hold Countdown
                            CircularTimerDisplay(
                                secondsRemaining = session.timerSecondsRemaining,
                                totalSeconds = session.initialTimerDuration,
                                size = 160.dp,
                                progressColor = VibrantPurple,
                                trackColor = VibrantTrack,
                                label = "HOLD"
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = onToggleTimer,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.testTag("btn_toggle_hold_timer")
                            ) {
                                Icon(
                                    imageVector = if (session.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (session.isTimerRunning) "Pause Hold" else "Start Hold")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Target muscles
                        if (exerciseDetail != null) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                exerciseDetail.targetMuscles.take(3).forEach { muscle ->
                                    MuscleTagChip(muscle = muscle)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Form Cues Box
                if (exerciseDetail != null) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "FORM CUES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantCyan,
                                    letterSpacing = 1.sp
                                )

                                Surface(
                                    color = VibrantPurpleContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { showVideoDemoDialog = true }
                                        .testTag("btn_workout_video_demo")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = null,
                                            tint = VibrantPurpleDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${exerciseDetail.videoDurationSeconds}s Demo",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = VibrantPurpleDark
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            exerciseDetail.formCues.forEach { cue ->
                                Row(
                                    modifier = Modifier.padding(vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = VibrantCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cue,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Bottom Action Button
        if (isResting) {
            Button(
                onClick = onSkipRest,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VibrantCyanContainer,
                    contentColor = VibrantCyan
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_skip_rest")
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = null,
                    tint = VibrantCyan
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Skip Rest & Start Next Set",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = VibrantCyan
                )
            }
        } else {
            Button(
                onClick = onCompleteSet,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_complete_set")
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (session.currentSet < currentRoutineExercise.sets) "Complete Set ${session.currentSet}" else "Finish Exercise",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
