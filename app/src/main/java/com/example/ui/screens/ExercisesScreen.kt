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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.MovementCategory
import com.example.ui.components.CategoryChip
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.MuscleTagChip
import com.example.ui.components.VideoDemonstrationDialog
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantGold
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDark

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExercisesScreen(
    exercises: List<Exercise>,
    searchQuery: String,
    selectedCategory: MovementCategory?,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (MovementCategory?) -> Unit,
    onSelectExercise: (Exercise) -> Unit,
    onLogPr: (Exercise) -> Unit,
    onPracticeTimer: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    var videoDemoExercise by remember { mutableStateOf<Exercise?>(null) }

    videoDemoExercise?.let { ex ->
        VideoDemonstrationDialog(
            exercise = ex,
            onDismiss = { videoDemoExercise = null }
        )
    }

    val filtered = exercises.filter { ex ->
        val matchesCategory = selectedCategory == null || ex.category == selectedCategory
        val matchesQuery = searchQuery.isBlank() ||
                ex.name.contains(searchQuery, ignoreCase = true) ||
                ex.targetMuscles.any { it.contains(searchQuery, ignoreCase = true) } ||
                ex.progressionChain.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search movement, muscle, or skill...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = VibrantPurple
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_exercises_input"),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VibrantPurple,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                singleLine = true
            )
        }

        // Category Filter
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAllSelected = selectedCategory == null
                    Surface(
                        color = if (isAllSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        border = if (isAllSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onSelectCategory(null) }
                            .testTag("exercise_filter_all")
                    ) {
                        Text(
                            text = "All Categories",
                            color = if (isAllSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                items(MovementCategory.values()) { cat ->
                    CategoryChip(
                        category = cat,
                        isSelected = selectedCategory == cat,
                        onClick = {
                            if (selectedCategory == cat) onSelectCategory(null) else onSelectCategory(cat)
                        }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROGRESSION TREE (${filtered.size})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Tap for form & cues",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = VibrantCyan
                )
            }
        }

        items(filtered, key = { it.id }) { exercise ->
            ExerciseCardItem(
                exercise = exercise,
                onClick = { onSelectExercise(exercise) },
                onLogPr = { onLogPr(exercise) },
                onPracticeTimer = { onPracticeTimer(exercise) },
                onWatchVideo = { videoDemoExercise = exercise }
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExerciseCardItem(
    exercise: Exercise,
    onClick: () -> Unit,
    onLogPr: () -> Unit,
    onPracticeTimer: () -> Unit,
    onWatchVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .testTag("exercise_item_${exercise.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DifficultyBadge(difficulty = exercise.difficulty)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = VibrantCyan.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(onClick = onWatchVideo)
                            .testTag("badge_video_demo_${exercise.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "Watch Demo",
                                tint = VibrantCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${exercise.videoDurationSeconds}s Demo",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VibrantCyan
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Surface(
                        color = VibrantPurpleContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Stage ${exercise.progressionRank}/5",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VibrantPurpleDark,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = exercise.progressionChain,
                fontSize = 12.sp,
                color = VibrantCyan,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                exercise.targetMuscles.forEach { muscle ->
                    MuscleTagChip(muscle = muscle)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Standard: ${exercise.defaultRepsOrSeconds}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Row {
                    IconButton(
                        onClick = onWatchVideo,
                        modifier = Modifier.testTag("btn_quick_video_${exercise.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Watch Demo Video",
                            tint = VibrantCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onLogPr,
                        modifier = Modifier.testTag("btn_quick_pr_${exercise.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Log PR",
                            tint = VibrantGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onPracticeTimer,
                        modifier = Modifier.testTag("btn_quick_timer_${exercise.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Practice Timer",
                            tint = VibrantPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onClick,
                        modifier = Modifier.testTag("btn_view_details_${exercise.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View Details",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
