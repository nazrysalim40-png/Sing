package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChecklistCategory
import com.example.data.model.ReleaseChecklistItem
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ReleaseUiStats

@Composable
fun ReadinessGauge(
    stats: ReleaseUiStats,
    checklist: List<ReleaseChecklistItem>,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = stats.completionPercentage / 100f,
        animationSpec = tween(durationMillis = 1000),
        label = "readiness_gauge"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ElectricViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.TaskAlt,
                            contentDescription = "Readiness",
                            tint = ElectricViolet,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Drop Readiness Meter",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${stats.completedChecklistCount} / ${stats.totalChecklistCount} Tasks",
                    color = HyperCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dual Progress bar & Gauge visualization
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF0F0D1B))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(ElectricViolet, HyperCyan, MintGreen)
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Category breakdown chips
            val categories = ChecklistCategory.values()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                categories.take(4).forEach { cat ->
                    val catItems = checklist.filter { it.category == cat }
                    val catDone = catItems.count { it.isCompleted }
                    val allDone = catItems.isNotEmpty() && catDone == catItems.size

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(if (allDone) MintGreen.copy(alpha = 0.2f) else StudioBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            if (allDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Done",
                                    tint = MintGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = "$catDone/${catItems.size}",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = when (cat) {
                                ChecklistCategory.AUDIO_MASTER -> "Audio"
                                ChecklistCategory.ARTWORK_ASSETS -> "Visuals"
                                ChecklistCategory.METADATA_LEGAL -> "Splits"
                                ChecklistCategory.DISTRIBUTION -> "DSP"
                                else -> "Promo"
                            },
                            color = if (allDone) MintGreen else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
