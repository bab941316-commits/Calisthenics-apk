package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.WorkoutLog
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantCyanBright
import com.example.ui.theme.VibrantCyanContainer
import com.example.ui.theme.VibrantGold
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDark
import com.example.ui.theme.VibrantPurpleLight
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Data structure representing a calendar day cell.
 */
data class MonthDay(
    val dayNumber: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean,
    val calendarYear: Int,
    val calendarMonth: Int, // 0-indexed
    val workouts: List<WorkoutLog>
)

/**
 * Visual monthly calendar view highlighting completed workout dates to encourage consistency.
 * Displays interactive 7-day grid, monthly consistency streaks, active training days, and
 * a detailed breakdown of workouts completed on the selected date.
 */
@Composable
fun MonthlyWorkoutCalendar(
    workoutLogs: List<WorkoutLog>,
    onSelectWorkout: (WorkoutLog) -> Unit = {},
    onDeleteLog: (Long) -> Unit = {},
    onStartWorkoutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val todayCal = remember { Calendar.getInstance() }
    val currentYear = todayCal.get(Calendar.YEAR)
    val currentMonth = todayCal.get(Calendar.MONTH)
    val currentDay = todayCal.get(Calendar.DAY_OF_MONTH)

    var displayYear by remember { mutableIntStateOf(currentYear) }
    var displayMonth by remember { mutableIntStateOf(currentMonth) } // 0-indexed
    var selectedDayNumber by remember { mutableIntStateOf(currentDay) }

    // Group workouts for the selected year and month by day of month
    val workoutsByDayOfMonth = remember(workoutLogs, displayYear, displayMonth) {
        val map = mutableMapOf<Int, MutableList<WorkoutLog>>()
        val tempCal = Calendar.getInstance()
        workoutLogs.forEach { log ->
            tempCal.timeInMillis = log.timestamp
            val y = tempCal.get(Calendar.YEAR)
            val m = tempCal.get(Calendar.MONTH)
            val d = tempCal.get(Calendar.DAY_OF_MONTH)
            if (y == displayYear && m == displayMonth) {
                map.getOrPut(d) { mutableListOf() }.add(log)
            }
        }
        map
    }

    // Month summary calculations
    val daysInMonth = remember(displayYear, displayMonth) {
        val cal = Calendar.getInstance()
        cal.set(displayYear, displayMonth, 1)
        cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    val activeDaysCount = remember(workoutsByDayOfMonth) {
        workoutsByDayOfMonth.keys.size
    }

    val totalWorkoutsInMonth = remember(workoutsByDayOfMonth) {
        workoutsByDayOfMonth.values.sumOf { it.size }
    }

    val totalMinutesInMonth = remember(workoutsByDayOfMonth) {
        workoutsByDayOfMonth.values.flatten().sumOf { it.durationSeconds } / 60
    }

    // Consistency rate calculation (active days / days passed or total days)
    val consistencyRate = remember(activeDaysCount, daysInMonth, displayYear, displayMonth, currentYear, currentMonth, currentDay) {
        val relevantDays = if (displayYear == currentYear && displayMonth == currentMonth) {
            maxOf(1, currentDay)
        } else {
            daysInMonth
        }
        ((activeDaysCount.toFloat() / relevantDays.toFloat()) * 100).toInt().coerceIn(0, 100)
    }

    // Current workout streak (consecutive days of training)
    val currentStreak = remember(workoutLogs) {
        calculateCurrentStreak(workoutLogs)
    }

    // Month name title
    val monthTitle = remember(displayYear, displayMonth) {
        val cal = Calendar.getInstance().apply { set(displayYear, displayMonth, 1) }
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_workout_calendar_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Month Navigation & Today Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WORKOUT CONSISTENCY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantPurple,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = monthTitle,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Previous Month
                    IconButton(
                        onClick = {
                            if (displayMonth == 0) {
                                displayMonth = 11
                                displayYear -= 1
                            } else {
                                displayMonth -= 1
                            }
                            selectedDayNumber = 1
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_prev_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous Month",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Jump to Today
                    val isShowingCurrentMonth = displayYear == currentYear && displayMonth == currentMonth
                    if (!isShowingCurrentMonth) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = VibrantCyan.copy(alpha = 0.15f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    displayYear = currentYear
                                    displayMonth = currentMonth
                                    selectedDayNumber = currentDay
                                }
                                .testTag("btn_jump_today")
                        ) {
                            Text(
                                text = "Today",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantCyanBright,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Next Month
                    IconButton(
                        onClick = {
                            if (displayMonth == 11) {
                                displayMonth = 0
                                displayYear += 1
                            } else {
                                displayMonth += 1
                            }
                            selectedDayNumber = 1
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_next_month")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Next Month",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Consistency & Streak Metrics Pill Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Streak Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = VibrantPurpleContainer.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, VibrantPurple.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = VibrantGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = if (currentStreak > 0) "$currentStreak-Day Streak" else "Start Streak",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = VibrantPurpleDark
                            )
                            Text(
                                text = "Consistency",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Active Days Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = VibrantCyanContainer.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, VibrantCyan.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = VibrantCyanBright,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "$activeDaysCount Active Days",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = VibrantCyanBright
                            )
                            Text(
                                text = "$totalWorkoutsInMonth sessions ($totalMinutesInMonth m)",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day of Week Header Row (Sun - Sat)
            val weekDayLabels = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                weekDayLabels.forEach { label ->
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 7-Column Calendar Grid
            val monthDays = remember(displayYear, displayMonth, workoutsByDayOfMonth, selectedDayNumber) {
                buildMonthGrid(
                    year = displayYear,
                    month = displayMonth,
                    workoutsByDay = workoutsByDayOfMonth,
                    currentYear = currentYear,
                    currentMonth = currentMonth,
                    currentDay = currentDay,
                    selectedDay = selectedDayNumber
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val weeks = monthDays.chunked(7)
                weeks.forEach { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        week.forEach { day ->
                            CalendarDayCell(
                                day = day,
                                onClick = {
                                    if (day.isCurrentMonth) {
                                        selectedDayNumber = day.dayNumber
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Calendar Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Workout Completed Legend
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(VibrantPurple, CircleShape)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Completed Workout",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Today Legend
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color.Transparent, CircleShape)
                        .padding(1.dp)
                        .clip(CircleShape)
                        .background(VibrantCyan)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "Today",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected Day Inspection Section
            val selectedWorkouts = workoutsByDayOfMonth[selectedDayNumber].orEmpty()
            val selectedDateStr = remember(displayYear, displayMonth, selectedDayNumber) {
                val cal = Calendar.getInstance().apply { set(displayYear, displayMonth, selectedDayNumber) }
                SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(cal.time)
            }

            SelectedDayWorkoutSection(
                dateString = selectedDateStr,
                workouts = selectedWorkouts,
                onDeleteLog = onDeleteLog,
                onStartWorkoutClick = onStartWorkoutClick
            )
        }
    }
}

/**
 * Individual Calendar Day Cell in the 7-day grid.
 */
@Composable
fun CalendarDayCell(
    day: MonthDay,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasWorkout = day.workouts.isNotEmpty()
    val isSelected = day.isSelected && day.isCurrentMonth
    val isToday = day.isToday && day.isCurrentMonth

    val containerColor = when {
        !day.isCurrentMonth -> Color.Transparent
        hasWorkout && isSelected -> VibrantPurple
        hasWorkout -> VibrantPurpleContainer
        isSelected -> VibrantCyan.copy(alpha = 0.2f)
        else -> Color.Transparent
    }

    val textColor = when {
        !day.isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.25f)
        hasWorkout && isSelected -> Color.White
        hasWorkout -> VibrantPurpleDark
        isSelected -> VibrantCyanBright
        isToday -> VibrantCyanBright
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderStroke = when {
        !day.isCurrentMonth -> null
        isSelected -> BorderStroke(2.dp, VibrantCyanBright)
        isToday -> BorderStroke(1.5.dp, VibrantCyan.copy(alpha = 0.8f))
        hasWorkout -> BorderStroke(1.dp, VibrantPurple.copy(alpha = 0.4f))
        else -> null
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = containerColor,
        border = borderStroke,
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = day.isCurrentMonth, onClick = onClick)
            .testTag("cal_day_${day.dayNumber}_${day.isCurrentMonth}")
    ) {
        Box(
            modifier = Modifier.padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${day.dayNumber}",
                    fontSize = 12.sp,
                    fontWeight = if (hasWorkout || isSelected || isToday) FontWeight.Black else FontWeight.Normal,
                    color = textColor
                )

                if (hasWorkout && day.isCurrentMonth) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(minOf(3, day.workouts.size)) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .background(
                                        if (isSelected) VibrantGold else VibrantPurple,
                                        CircleShape
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Breakdown section displayed underneath the monthly calendar showing workouts for the selected date.
 */
@Composable
fun SelectedDayWorkoutSection(
    dateString: String,
    workouts: List<WorkoutLog>,
    onDeleteLog: (Long) -> Unit,
    onStartWorkoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("selected_day_workout_section")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = VibrantPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = dateString,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (workouts.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = VibrantPurpleContainer
                    ) {
                        Text(
                            text = "${workouts.size} ${if (workouts.size == 1) "Workout" else "Workouts"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = VibrantPurpleDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (workouts.isEmpty()) {
                // Rest Day encouraging message
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Rest & Recovery Day",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Consistency includes smart recovery for tendon adaptation.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = onStartWorkoutClick,
                        modifier = Modifier.testTag("btn_train_on_date")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = VibrantCyan,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Train Now",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantCyan
                        )
                    }
                }
            } else {
                // List of workouts on this date
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    workouts.forEach { log ->
                        val minutes = log.durationSeconds / 60
                        val seconds = log.durationSeconds % 60
                        val formattedDuration = String.format("%02d:%02d", minutes, seconds)
                        val timeFormatted = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(log.timestamp))

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, VibrantPurple.copy(alpha = 0.25f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("selected_day_log_${log.id}")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = log.routineTitle,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )

                                    IconButton(
                                        onClick = { onDeleteLog(log.id) },
                                        modifier = Modifier
                                            .size(22.dp)
                                            .testTag("btn_delete_calendar_log_${log.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete Workout",
                                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
                                            contentDescription = null,
                                            tint = VibrantPurple,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "$formattedDuration ($timeFormatted)",
                                            fontSize = 11.sp,
                                            color = VibrantPurple,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = VibrantCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "${log.exercisesCompleted} exercises",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        repeat(log.perceivedExertion) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = VibrantGold,
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }
                                }

                                if (log.notes.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "\"${log.notes}\"",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calculates current streak in days.
 */
private fun calculateCurrentStreak(logs: List<WorkoutLog>): Int {
    if (logs.isEmpty()) return 0

    val daySet = mutableSetOf<String>()
    val cal = Calendar.getInstance()
    logs.forEach { log ->
        cal.timeInMillis = log.timestamp
        val key = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
        daySet.add(key)
    }

    val checkCal = Calendar.getInstance()
    var streak = 0

    // Check today
    val todayKey = "${checkCal.get(Calendar.YEAR)}-${checkCal.get(Calendar.MONTH)}-${checkCal.get(Calendar.DAY_OF_MONTH)}"
    val trainedToday = daySet.contains(todayKey)

    if (trainedToday) {
        streak++
        checkCal.add(Calendar.DAY_OF_YEAR, -1)
    } else {
        // If haven't trained today yet, check yesterday to keep streak active
        checkCal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayKey = "${checkCal.get(Calendar.YEAR)}-${checkCal.get(Calendar.MONTH)}-${checkCal.get(Calendar.DAY_OF_MONTH)}"
        if (!daySet.contains(yesterdayKey)) {
            return 0
        }
    }

    // Continue counting backwards
    while (true) {
        val key = "${checkCal.get(Calendar.YEAR)}-${checkCal.get(Calendar.MONTH)}-${checkCal.get(Calendar.DAY_OF_MONTH)}"
        if (daySet.contains(key)) {
            streak++
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        } else {
            break
        }
    }

    return streak
}

/**
 * Builds the 7x5 or 7x6 month grid including padding days from previous and next months.
 */
private fun buildMonthGrid(
    year: Int,
    month: Int,
    workoutsByDay: Map<Int, List<WorkoutLog>>,
    currentYear: Int,
    currentMonth: Int,
    currentDay: Int,
    selectedDay: Int
): List<MonthDay> {
    val result = mutableListOf<MonthDay>()

    val cal = Calendar.getInstance()
    cal.set(year, month, 1)
    val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 1 = Sunday
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Previous month padding
    val prevCal = Calendar.getInstance().apply { set(year, month - 1, 1) }
    val daysInPrevMonth = prevCal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingDays = firstDayOfWeek - 1 // Days before Sunday (0 for Sun, 1 for Mon, etc.)

    for (i in leadingDays downTo 1) {
        val prevDayNumber = daysInPrevMonth - i + 1
        result.add(
            MonthDay(
                dayNumber = prevDayNumber,
                isCurrentMonth = false,
                isToday = false,
                isSelected = false,
                calendarYear = if (month == 0) year - 1 else year,
                calendarMonth = if (month == 0) 11 else month - 1,
                workouts = emptyList()
            )
        )
    }

    // Current month days
    for (d in 1..daysInMonth) {
        val isToday = (year == currentYear && month == currentMonth && d == currentDay)
        val isSelected = (d == selectedDay)
        val dayWorkouts = workoutsByDay[d].orEmpty()

        result.add(
            MonthDay(
                dayNumber = d,
                isCurrentMonth = true,
                isToday = isToday,
                isSelected = isSelected,
                calendarYear = year,
                calendarMonth = month,
                workouts = dayWorkouts
            )
        )
    }

    // Trailing padding to make complete weeks (multiple of 7)
    val remainingDays = (7 - (result.size % 7)) % 7
    for (d in 1..remainingDays) {
        result.add(
            MonthDay(
                dayNumber = d,
                isCurrentMonth = false,
                isToday = false,
                isSelected = false,
                calendarYear = if (month == 11) year + 1 else year,
                calendarMonth = if (month == 11) 0 else month + 1,
                workouts = emptyList()
            )
        )
    }

    return result
}
