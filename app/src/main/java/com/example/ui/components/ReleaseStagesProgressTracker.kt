package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChecklistCategory
import com.example.data.model.ReleaseChecklistItem
import com.example.data.model.SongRelease
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletDark
import com.example.ui.theme.ElectricVioletLight
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.HyperCyanDark
import com.example.ui.theme.MintGreen
import com.example.ui.theme.MintGreenDark
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Music Release Stages Definition with specific color, icon, milestone, and checklist category mapping.
 */
enum class MusicReleaseStage(
    val stageId: String,
    val title: String,
    val shortName: String,
    val description: String,
    val icon: ImageVector,
    val color: Color,
    val gradientColors: List<Color>,
    val defaultMilestone: String,
    val category: ChecklistCategory,
    val typicalLeadTimeWeeks: Int
) {
    MIXING(
        stageId = "mixing",
        title = "Mixing & Production",
        shortName = "Mixing",
        description = "Stem balancing, EQ, dynamics, and final stereo mix freeze.",
        icon = Icons.Default.GraphicEq,
        color = HyperCyan,
        gradientColors = listOf(HyperCyan, Color(0xFF009AB3)),
        defaultMilestone = "4-5 Weeks Out",
        category = ChecklistCategory.AUDIO_MASTER,
        typicalLeadTimeWeeks = 5
    ),
    MASTERING(
        stageId = "mastering",
        title = "Mastering & QC",
        shortName = "Mastering",
        description = "Loudness target (-14 LUFS), lossless WAV exports & audio QC.",
        icon = Icons.Default.Headphones,
        color = ElectricViolet,
        gradientColors = listOf(ElectricVioletLight, ElectricViolet),
        defaultMilestone = "4 Weeks Out",
        category = ChecklistCategory.AUDIO_MASTER,
        typicalLeadTimeWeeks = 4
    ),
    ARTWORK(
        stageId = "artwork",
        title = "Visuals & Artwork",
        shortName = "Artwork",
        description = "3000x3000px cover art, Spotify Canvas, motion teasers.",
        icon = Icons.Default.Image,
        color = NeonPink,
        gradientColors = listOf(NeonPink, Color(0xFFD81B60)),
        defaultMilestone = "3-4 Weeks Out",
        category = ChecklistCategory.ARTWORK_ASSETS,
        typicalLeadTimeWeeks = 4
    ),
    METADATA(
        stageId = "metadata",
        title = "Metadata & Splits",
        shortName = "Metadata",
        description = "ISRC/UPC sync, songwriter splits, PRO registration.",
        icon = Icons.Default.VerifiedUser,
        color = AmberWarning,
        gradientColors = listOf(AmberWarning, Color(0xFFFF8F00)),
        defaultMilestone = "3 Weeks Out",
        category = ChecklistCategory.METADATA_LEGAL,
        typicalLeadTimeWeeks = 3
    ),
    DISTRIBUTION(
        stageId = "distribution",
        title = "Distribution & Pitching",
        shortName = "Distribution",
        description = "Distributor upload, Spotify for Artists & Apple Music pitch.",
        icon = Icons.Default.Send,
        color = Color(0xFF00E5FF),
        gradientColors = listOf(Color(0xFF00E5FF), Color(0xFF0097A7)),
        defaultMilestone = "2-4 Weeks Out",
        category = ChecklistCategory.DISTRIBUTION,
        typicalLeadTimeWeeks = 3
    ),
    PROMO(
        stageId = "promo",
        title = "Marketing & Promo",
        shortName = "Promo",
        description = "Pre-save campaign, TikTok/Reels teasers, EPK blog pitching.",
        icon = Icons.Default.Campaign,
        color = Color(0xFFFF5252),
        gradientColors = listOf(Color(0xFFFF5252), Color(0xFFE53935)),
        defaultMilestone = "1-2 Weeks Out",
        category = ChecklistCategory.MARKETING_PROMO,
        typicalLeadTimeWeeks = 2
    ),
    DROP_DAY(
        stageId = "drop_day",
        title = "Drop Day Launch",
        shortName = "Launch",
        description = "DSP live verification, streaming bio links, fan broadcast.",
        icon = Icons.Default.Celebration,
        color = MintGreen,
        gradientColors = listOf(MintGreen, MintGreenDark),
        defaultMilestone = "Drop Day (0d)",
        category = ChecklistCategory.DROP_DAY,
        typicalLeadTimeWeeks = 0
    )
}

/**
 * Calculated progress status for an individual release stage.
 */
data class StageProgressData(
    val stage: MusicReleaseStage,
    val completedTasks: Int,
    val totalTasks: Int,
    val completionPercentage: Float, // 0.0 to 100.0
    val tasks: List<ReleaseChecklistItem>,
    val status: StageStatus
)

enum class StageStatus(val label: String, val badgeColor: Color) {
    COMPLETED("Completed", MintGreen),
    IN_PROGRESS("In Progress", HyperCyan),
    NOT_STARTED("Not Started", TextMuted)
}

/**
 * Visual Progress Tracker for Music Release Stages.
 * Features:
 * - Multi-stage bar charts and radial progress visualizer with animated fill
 * - Detailed breakdown of stages (Mixing, Mastering, Artwork, Metadata, Distribution, Promo, Launch)
 * - Interactive stage selector to inspect tasks and milestone countdowns
 * - Live task toggling with immediate progress recalculation
 * - Timeline and pipeline graph representations
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ReleaseStagesProgressTracker(
    release: SongRelease?,
    checklist: List<ReleaseChecklistItem>,
    onToggleTask: (ReleaseChecklistItem) -> Unit,
    onNavigateToChecklist: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Chart View, 1: Pipeline Flow, 2: Stage Tasks
    var selectedStageIndex by remember { mutableIntStateOf(0) }

    // Map checklist items into each stage
    val stageDataList = remember(checklist) {
        MusicReleaseStage.values().map { stage ->
            val stageTasks = checklist.filter { it.category == stage.category }
            val completed = stageTasks.count { it.isCompleted }
            val total = stageTasks.size
            val pct = if (total > 0) (completed.toFloat() / total.toFloat()) * 100f else 0f
            val status = when {
                total > 0 && completed == total -> StageStatus.COMPLETED
                completed > 0 -> StageStatus.IN_PROGRESS
                else -> StageStatus.NOT_STARTED
            }
            StageProgressData(
                stage = stage,
                completedTasks = completed,
                totalTasks = total,
                completionPercentage = pct,
                tasks = stageTasks,
                status = status
            )
        }
    }

    val overallTotalTasks = checklist.size
    val overallCompletedTasks = checklist.count { it.isCompleted }
    val overallPercentage = if (overallTotalTasks > 0) {
        (overallCompletedTasks.toFloat() / overallTotalTasks.toFloat()) * 100f
    } else 0f

    val completedStagesCount = stageDataList.count { it.status == StageStatus.COMPLETED }
    val inProgressStagesCount = stageDataList.count { it.status == StageStatus.IN_PROGRESS }

    val activeStageData = stageDataList.getOrElse(selectedStageIndex) { stageDataList.first() }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_release_stages_progress_tracker")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(ElectricViolet.copy(alpha = 0.3f), HyperCyan.copy(alpha = 0.3f))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = HyperCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = "PRODUCTION & ROLLOUT PIPELINE",
                            color = HyperCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.3.sp
                        )
                        Text(
                            text = "Release Stages Tracker",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Overall Score Badge
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = when {
                        overallPercentage >= 100f -> MintGreen.copy(alpha = 0.18f)
                        overallPercentage > 50f -> HyperCyan.copy(alpha = 0.18f)
                        else -> ElectricViolet.copy(alpha = 0.18f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when {
                            overallPercentage >= 100f -> MintGreen.copy(alpha = 0.4f)
                            overallPercentage > 50f -> HyperCyan.copy(alpha = 0.4f)
                            else -> ElectricViolet.copy(alpha = 0.4f)
                        }
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "${"%.0f".format(overallPercentage)}%",
                            color = when {
                                overallPercentage >= 100f -> MintGreen
                                overallPercentage > 50f -> HyperCyan
                                else -> ElectricViolet
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "READY",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Tab Row (Chart Visualizer vs Pipeline Flow vs Stage Tasks)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = StudioSurfaceHover,
                contentColor = HyperCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = HyperCyan,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Progress Chart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Pipeline Flow", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FormatListBulleted, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Stage Tasks (${activeStageData.tasks.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stage Horizontal Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(stageDataList.indices.toList()) { index ->
                    val data = stageDataList[index]
                    val isSelected = selectedStageIndex == index

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) data.stage.color.copy(alpha = 0.22f) else StudioSurfaceHover,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) data.stage.color else StudioBorder
                        ),
                        modifier = Modifier
                            .clickable {
                                selectedStageIndex = index
                            }
                            .testTag("chip_stage_${data.stage.stageId}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = data.stage.icon,
                                contentDescription = null,
                                tint = if (isSelected) data.stage.color else TextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = data.stage.shortName,
                                color = if (isSelected) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(6.dp))

                            // Mini Status Indicator Dot
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when (data.status) {
                                            StageStatus.COMPLETED -> MintGreen
                                            StageStatus.IN_PROGRESS -> data.stage.color
                                            StageStatus.NOT_STARTED -> TextMuted
                                        }
                                    )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Content based on Selected Tab
            when (selectedTab) {
                0 -> {
                    // TAB 0: Multi-Stage Bar Chart & Radial Visualizer
                    StagesChartView(
                        stageDataList = stageDataList,
                        selectedStageIndex = selectedStageIndex,
                        onSelectStage = { selectedStageIndex = it },
                        overallPercentage = overallPercentage
                    )
                }
                1 -> {
                    // TAB 1: Connected Pipeline Flow View
                    StagesPipelineFlowView(
                        stageDataList = stageDataList,
                        selectedStageIndex = selectedStageIndex,
                        onSelectStage = {
                            selectedStageIndex = it
                            selectedTab = 2 // jump to tasks
                        }
                    )
                }
                2 -> {
                    // TAB 2: Stage Tasks Drilldown & Toggles
                    StageTasksDetailView(
                        stageData = activeStageData,
                        onToggleTask = onToggleTask,
                        onNavigateToChecklist = onNavigateToChecklist
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Summary Banner
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StudioSurfaceHover,
                border = BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = HyperCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$completedStagesCount/${stageDataList.size} Stages Completed • $overallCompletedTasks/$overallTotalTasks Tasks Done",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "View Checklist →",
                        color = HyperCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToChecklist() }
                    )
                }
            }
        }
    }
}

/**
 * Visual Multi-Bar Chart and Radial Completion View inspired by Recharts data visualization.
 */
@Composable
private fun StagesChartView(
    stageDataList: List<StageProgressData>,
    selectedStageIndex: Int,
    onSelectStage: (Int) -> Unit,
    overallPercentage: Float
) {
    val animatedOverall by animateFloatAsState(
        targetValue = overallPercentage,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "overall_pct"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        // High Level Metrics Row (Radial Gauge + Stage Counters)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(StudioBackground.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .border(BorderStroke(1.dp, StudioBorder), RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Radial Progress Ring
            Box(
                modifier = Modifier.size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 8.dp.toPx()
                    val radius = (size.minDimension - strokeWidth) / 2
                    val center = Offset(size.width / 2, size.height / 2)

                    // Background Track
                    drawCircle(
                        color = StudioSurfaceHover,
                        radius = radius,
                        center = center,
                        style = Stroke(width = strokeWidth)
                    )

                    // Animated Progress Arc
                    val sweepAngle = (animatedOverall / 100f) * 360f
                    drawArc(
                        brush = Brush.sweepGradient(
                            listOf(HyperCyan, ElectricViolet, MintGreen, HyperCyan)
                        ),
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${animatedOverall.toInt()}%",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "TOTAL",
                        color = TextMuted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Right: Stages Status Breakdown Summary
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val completed = stageDataList.count { it.status == StageStatus.COMPLETED }
                val inProgress = stageDataList.count { it.status == StageStatus.IN_PROGRESS }
                val notStarted = stageDataList.count { it.status == StageStatus.NOT_STARTED }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Release Pipeline Health", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${stageDataList.size} Stages", color = HyperCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusMiniPill(label = "Done", count = completed, color = MintGreen, modifier = Modifier.weight(1f))
                    StatusMiniPill(label = "Active", count = inProgress, color = HyperCyan, modifier = Modifier.weight(1f))
                    StatusMiniPill(label = "Queue", count = notStarted, color = TextMuted, modifier = Modifier.weight(1f))
                }

                Text(
                    text = "Tap any bar below to view stage milestones & tasks.",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Custom Visual Progress Chart (Recharts-style multi-bar layout)
        Text(
            text = "STAGE COMPLETION METRICS (%)",
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(10.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = StudioBackground.copy(alpha = 0.6f),
            border = BorderStroke(1.dp, StudioBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Interactive Bars for each Music Release Stage
                stageDataList.forEachIndexed { index, data ->
                    val isSelected = selectedStageIndex == index
                    val animPct by animateFloatAsState(
                        targetValue = data.completionPercentage,
                        animationSpec = tween(durationMillis = 600 + (index * 60), easing = FastOutSlowInEasing),
                        label = "stage_bar_${data.stage.stageId}"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectStage(index) }
                            .padding(vertical = 5.dp)
                            .testTag("bar_stage_${data.stage.stageId}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(data.stage.color.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = data.stage.icon,
                                        contentDescription = null,
                                        tint = data.stage.color,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = data.stage.title,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${data.completedTasks}/${data.totalTasks} tasks",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${"%.0f".format(animPct)}%",
                                    color = if (animPct >= 100f) MintGreen else data.stage.color,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        // Progress Track & Fill
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(StudioSurfaceHover)
                        ) {
                            val fraction = (animPct / 100f).coerceIn(0f, 1f)
                            if (fraction > 0f) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction)
                                        .fillMaxHeight()
                                        .background(
                                            Brush.horizontalGradient(
                                                data.stage.gradientColors
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Interactive Timeline / Pipeline Node Flow View
 */
@Composable
private fun StagesPipelineFlowView(
    stageDataList: List<StageProgressData>,
    selectedStageIndex: Int,
    onSelectStage: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StudioBackground.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, StudioBorder), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Text(
            text = "RELEASE MILESTONES & STAGE TIMELINE",
            color = HyperCyan,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        stageDataList.forEachIndexed { index, data ->
            val isLast = index == stageDataList.size - 1
            val isSelected = selectedStageIndex == index

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectStage(index) }
                    .padding(vertical = 2.dp)
            ) {
                // Vertical Pipeline Line & Node
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(
                                when (data.status) {
                                    StageStatus.COMPLETED -> MintGreen.copy(alpha = 0.25f)
                                    StageStatus.IN_PROGRESS -> data.stage.color.copy(alpha = 0.25f)
                                    StageStatus.NOT_STARTED -> StudioSurfaceHover
                                }
                            )
                            .border(
                                1.5.dp,
                                when (data.status) {
                                    StageStatus.COMPLETED -> MintGreen
                                    StageStatus.IN_PROGRESS -> data.stage.color
                                    StageStatus.NOT_STARTED -> StudioBorder
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (data.status == StageStatus.COMPLETED) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = MintGreen, modifier = Modifier.size(14.dp))
                        } else {
                            Icon(data.stage.icon, contentDescription = null, tint = data.stage.color, modifier = Modifier.size(13.dp))
                        }
                    }

                    if (!isLast) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(38.dp)
                                .background(
                                    if (data.status == StageStatus.COMPLETED) MintGreen.copy(alpha = 0.5f)
                                    else StudioBorder
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Stage Info & Milestones
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = if (isLast) 0.dp else 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.stage.title,
                            color = if (isSelected) HyperCyan else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = data.status.badgeColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, data.status.badgeColor.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = data.status.label,
                                color = data.status.badgeColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "${data.stage.defaultMilestone} • ${data.stage.description}",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LinearProgressIndicator(
                            progress = (data.completionPercentage / 100f).coerceIn(0f, 1f),
                            color = data.stage.color,
                            trackColor = StudioSurfaceHover,
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${"%.0f".format(data.completionPercentage)}%",
                            color = data.stage.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Detailed Task Drilldown and Interactive Checkbox View for the Selected Stage
 */
@Composable
private fun StageTasksDetailView(
    stageData: StageProgressData,
    onToggleTask: (ReleaseChecklistItem) -> Unit,
    onNavigateToChecklist: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(StudioBackground.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, stageData.stage.color.copy(alpha = 0.4f)), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        // Stage Header & Target Milestone
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(stageData.stage.color.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = stageData.stage.icon,
                        contentDescription = null,
                        tint = stageData.stage.color,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = stageData.stage.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Milestone: ${stageData.stage.defaultMilestone}",
                        color = stageData.stage.color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = stageData.stage.color.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${stageData.completedTasks}/${stageData.totalTasks} Done",
                    color = stageData.stage.color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stageData.stage.description,
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = StudioBorder)
        Spacer(modifier = Modifier.height(12.dp))

        // Task Items for this Stage
        if (stageData.tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No custom tasks added for this stage yet.",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }
        } else {
            stageData.tasks.forEach { task ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (task.isCompleted) MintGreen.copy(alpha = 0.08f) else StudioSurfaceHover,
                    border = BorderStroke(
                        1.dp,
                        if (task.isCompleted) MintGreen.copy(alpha = 0.3f) else StudioBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onToggleTask(task) }
                        .testTag("task_item_${task.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onToggleTask(task) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = if (task.isCompleted) "Completed" else "Incomplete",
                                tint = if (task.isCompleted) MintGreen else TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                color = if (task.isCompleted) TextMuted else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.Bold
                            )
                            if (task.description.isNotBlank()) {
                                Text(
                                    text = task.description,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusMiniPill(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$count $label",
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
