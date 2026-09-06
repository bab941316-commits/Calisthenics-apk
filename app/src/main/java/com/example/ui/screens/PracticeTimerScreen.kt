package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CalisthenicsData
import com.example.data.model.Exercise
import com.example.ui.components.CircularTimerDisplay
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantCyanBright
import com.example.ui.theme.VibrantCyanContainer
import com.example.ui.theme.VibrantGold
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurpleLight
import com.example.ui.theme.VibrantTrack

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeTimerScreen(
    secondsRemaining: Int,
    initialSeconds: Int,
    isRunning: Boolean,
    onToggleTimer: () -> Unit,
    onResetTimer: (Int) -> Unit,
    onStartTimerWithDuration: (Int) -> Unit,
    onSaveBenchPr: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    val presets = listOf(15, 30, 45, 60, 90, 120)
    val benchmarkExercises = CalisthenicsData.allExercises.filter { it.isTimedHold }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "HOLD & REST TRAINER",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = VibrantPurple,
            letterSpacing = 1.sp
        )

        Text(
            text = "Isometric Skill Timer",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Large Circular Timer with Vibrant Palette
        CircularTimerDisplay(
            secondsRemaining = secondsRemaining,
            totalSeconds = initialSeconds,
            size = 220.dp,
            strokeWidth = 12.dp,
            progressColor = if (isRunning) VibrantCyanBright else VibrantPurple,
            trackColor = VibrantTrack,
            label = if (isRunning) "ACTIVE HOLD" else "TIMER READY"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Timer Controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { onResetTimer(initialSeconds) },
                shape = CircleShape,
                modifier = Modifier
                    .size(54.dp)
                    .testTag("btn_practice_reset")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = onToggleTimer,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) VibrantCyanContainer else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .height(56.dp)
                    .width(180.dp)
                    .testTag("btn_practice_toggle")
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = if (isRunning) VibrantCyan else MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRunning) "Pause" else "Start Timer",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = if (isRunning) VibrantCyan else MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Interval Preset Buttons
        Text(
            text = "QUICK INTERVAL PRESETS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            presets.forEach { sec ->
                val isCurrent = initialSeconds == sec
                Surface(
                    color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    border = if (isCurrent) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onStartTimerWithDuration(sec) }
                        .testTag("preset_${sec}s")
                ) {
                    Text(
                        text = "${sec}s",
                        color = if (isCurrent) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // Benchmark Hold Testing
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STATIC BENCHMARK HOLDS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantPurple,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Auto-set & test",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                benchmarkExercises.forEach { benchEx ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = benchEx.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Standard: ${benchEx.holdDurationSeconds}s • ${benchEx.difficulty.displayName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { onStartTimerWithDuration(benchEx.holdDurationSeconds) },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.testTag("btn_load_bench_${benchEx.id}")
                            ) {
                                Text("Load ${benchEx.holdDurationSeconds}s", fontSize = 12.sp)
                            }

                            IconButton(
                                onClick = { onSaveBenchPr(benchEx) },
                                modifier = Modifier.testTag("btn_save_bench_pr_${benchEx.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Save PR",
                                    tint = VibrantGold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
