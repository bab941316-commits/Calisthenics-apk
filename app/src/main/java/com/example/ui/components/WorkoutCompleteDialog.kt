package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.db.WorkoutLog
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantGold
import com.example.ui.theme.VibrantPurple

@Composable
fun WorkoutCompleteDialog(
    completedLog: WorkoutLog,
    onSave: (perceivedExertion: Int, notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    var exertionRating by remember { mutableIntStateOf(3) }
    var notesText by remember { mutableStateOf("") }

    val exertionLabels = listOf(
        "1 - Light & Easy",
        "2 - Moderate Warmup",
        "3 - Solid Challenge",
        "4 - Heavy Beast Mode",
        "5 - Absolute Failure"
    )

    val minutes = completedLog.durationSeconds / 60
    val seconds = completedLog.durationSeconds % 60
    val formattedDuration = String.format("%02d:%02d", minutes, seconds)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("workout_complete_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Workout Complete",
                    tint = VibrantGold,
                    modifier = Modifier.size(54.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "WORKOUT CRUSHED!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = completedLog.routineTitle,
                    fontSize = 14.sp,
                    color = VibrantPurple,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SummaryStatCard(
                        value = formattedDuration,
                        label = "DURATION",
                        accentColor = VibrantPurple,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        value = "${completedLog.exercisesCompleted}/${completedLog.totalExercises}",
                        label = "EXERCISES",
                        accentColor = VibrantCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Rating selector
                Text(
                    text = "HOW INTENSE WAS IT? (RPE)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        val isSelected = i <= exertionRating
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating $i",
                            tint = if (isSelected) VibrantGold else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { exertionRating = i }
                                .padding(4.dp)
                                .testTag("rate_star_$i")
                        )
                    }
                }

                Text(
                    text = exertionLabels.getOrElse(exertionRating - 1) { "" },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VibrantPurple
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Optional notes
                OutlinedTextField(
                    value = notesText,
                    onValueChange = { notesText = it },
                    label = { Text("Personal Session Notes") },
                    placeholder = { Text("e.g. Clean form on pull-ups, felt explosive") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workout_notes_input"),
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onSave(exertionRating, notesText)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_save_workout_log")
                ) {
                    Text(
                        text = "Save Workout & View Stats",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("btn_dismiss_summary")
                ) {
                    Text(
                        text = "Discard",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
