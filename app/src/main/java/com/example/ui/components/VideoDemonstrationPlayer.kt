package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.example.data.model.Exercise
import com.example.data.model.MovementPhase
import com.example.ui.theme.VibrantCyan
import com.example.ui.theme.VibrantCyanBright
import com.example.ui.theme.VibrantGold
import com.example.ui.theme.VibrantPurple
import com.example.ui.theme.VibrantPurpleContainer
import com.example.ui.theme.VibrantPurpleDark
import kotlinx.coroutines.delay

/**
 * Main Video Demonstration Player component for displaying short 15-30s calisthenics form videos.
 * Features an embedded WebView player with direct external YouTube link support and an interactive
 * movement phase breakdown guide.
 */
@Composable
fun VideoDemonstrationPlayer(
    exercise: Exercise,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Video Player, 1: Form Phases Guide

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_demo_card_${exercise.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Duration and Type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Demonstration",
                        tint = VibrantPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MOVEMENT DEMONSTRATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantPurple,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    color = VibrantCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "${exercise.videoDurationSeconds}s Form Demo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = VibrantCyanBright,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Player Mode Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .height(38.dp)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Embedded Video",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("tab_embedded_video")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Phase Breakdown",
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    modifier = Modifier.testTag("tab_phase_breakdown")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState = selectedTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "video_player_mode"
            ) { tab ->
                when (tab) {
                    0 -> {
                        Column {
                            EmbeddedVideoView(
                                embedUrl = exercise.videoEmbedUrl,
                                fallbackVideoUrl = exercise.videoUrl,
                                exerciseName = exercise.name,
                                durationSeconds = exercise.videoDurationSeconds,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(16f / 9f)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Action buttons: Open link externally & Quick hint
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "15-30s execution form",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                OutlinedButton(
                                    onClick = {
                                        if (exercise.videoUrl.isNotBlank()) {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(exercise.videoUrl))
                                            context.startActivity(intent)
                                        }
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    border = BorderStroke(1.dp, VibrantPurple),
                                    modifier = Modifier.testTag("btn_open_external_video")
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                        contentDescription = "Open Video Link",
                                        tint = VibrantPurple,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Open in Video App",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = VibrantPurple
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        InteractivePhaseGuide(
                            phases = exercise.movementPhases,
                            totalDurationSeconds = exercise.videoDurationSeconds,
                            onWatchVideo = { selectedTab = 0 }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Embedded WebView player that loads video demonstration with graceful fallback.
 */
@Composable
fun EmbeddedVideoView(
    embedUrl: String,
    fallbackVideoUrl: String,
    exerciseName: String,
    durationSeconds: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var loadFailed by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    val embedHtml = remember(embedUrl) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { box-sizing: border-box; }
                body { 
                    margin: 0; 
                    padding: 0; 
                    background: #110E18; 
                    overflow: hidden; 
                    display: flex; 
                    align-items: center; 
                    justify-content: center; 
                    height: 100vh; 
                    font-family: sans-serif;
                }
                iframe { 
                    width: 100vw; 
                    height: 100vh; 
                    border: 0; 
                }
            </style>
        </head>
        <body>
            <iframe 
                src="$embedUrl?playsinline=1&rel=0&modestbranding=1&controls=1" 
                frameborder="0" 
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                allowfullscreen>
            </iframe>
        </body>
        </html>
        """.trimIndent()
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.destroy()
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF13101C))
            .testTag("embedded_video_player_container"),
        contentAlignment = Alignment.Center
    ) {
        if (!loadFailed) {
            AndroidView(
                factory = { ctx ->
                    try {
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.mediaPlaybackRequiresUserGesture = false
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            webChromeClient = WebChromeClient()
                            webViewClient = object : WebViewClient() {
                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                }
                            }
                            loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                            webViewRef = this
                        }
                    } catch (e: Throwable) {
                        loadFailed = true
                        android.view.View(ctx)
                    }
                },
                update = { webView ->
                    if (webView is WebView) {
                        webView.loadDataWithBaseURL("https://www.youtube.com", embedHtml, "text/html", "UTF-8", null)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        if (loadFailed) {
            // Fallback display with direct play button
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = VibrantCyan,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "$exerciseName Video Demo",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "$durationSeconds seconds short demo",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackVideoUrl))
                        context.startActivity(intent)
                    }
                ) {
                    Text("Watch on YouTube")
                }
            }
        }
    }
}

/**
 * Interactive Phase Guide allowing users to scrub and inspect each movement phase step-by-step
 * with exact form focus cues and animated visual timeline.
 */
@Composable
fun InteractivePhaseGuide(
    phases: List<MovementPhase>,
    totalDurationSeconds: Int,
    onWatchVideo: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (phases.isEmpty()) {
        Text(
            text = "Form phases are shown directly in the video demonstration above.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        return
    }

    var isPlaying by remember { mutableStateOf(false) }
    var currentSeconds by remember { mutableIntStateOf(0) }

    // Auto-advance when playing
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            delay(1000L)
            currentSeconds = (currentSeconds + 1) % (totalDurationSeconds + 1)
        }
    }

    // Determine current phase based on timestamp
    val activePhase = remember(currentSeconds, phases) {
        phases.lastOrNull { it.timestampSeconds <= currentSeconds } ?: phases.first()
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Active Phase Display Card
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, VibrantCyan.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = VibrantPurpleContainer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${phases.indexOf(activePhase) + 1}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = VibrantPurpleDark
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = activePhase.phaseName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Text(
                        text = "0:${String.format("%02d", currentSeconds)} / 0:${String.format("%02d", totalDurationSeconds)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = VibrantCyanBright
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = activePhase.formFocus,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Timeline Progress Bar
        val progress = if (totalDurationSeconds > 0) currentSeconds.toFloat() / totalDurationSeconds.toFloat() else 0f
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = VibrantCyan,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Phase chips for direct jumping
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            phases.forEach { phase ->
                val isCurrent = phase == activePhase
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isCurrent) VibrantPurple else MaterialTheme.colorScheme.surface,
                    border = BorderStroke(
                        1.dp,
                        if (isCurrent) VibrantPurple else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            currentSeconds = phase.timestampSeconds
                        }
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "0:${String.format("%02d", phase.timestampSeconds)}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = phase.phaseName,
                            fontSize = 10.sp,
                            maxLines = 1,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Controls: Rewind, Play/Pause, Fast-Forward, Switch to Video
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = {
                        currentSeconds = maxOf(0, currentSeconds - 5)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "Rewind 5s",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = {
                        currentSeconds = minOf(totalDurationSeconds, currentSeconds + 5)
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Forward 5s",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TextButton(
                onClick = onWatchVideo,
                modifier = Modifier.testTag("btn_switch_to_video")
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = VibrantPurple,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Watch Full Video",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantPurple
                )
            }
        }
    }
}

/**
 * Dedicated Fullscreen / Large Dialog for watching the video demonstration with all controls.
 */
@Composable
fun VideoDemonstrationDialog(
    exercise: Exercise,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("video_demonstration_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = exercise.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${exercise.progressionChain} • Stage ${exercise.progressionRank}/5",
                            fontSize = 12.sp,
                            color = VibrantCyan
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_video_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // The complete video player
                VideoDemonstrationPlayer(
                    exercise = exercise,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Key cues reminder
                Text(
                    text = "CORRECT FORM FOCUS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = VibrantPurple,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                exercise.formCues.take(2).forEach { cue ->
                    Text(
                        text = "• $cue",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Done")
                }
            }
        }
    }
}
