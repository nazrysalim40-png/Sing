package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ChecklistCategory
import com.example.data.model.ReleaseChecklistItem
import com.example.data.model.SongRelease
import com.example.ui.components.AudioWaveformBar
import com.example.ui.components.NewReleaseDialog
import com.example.ui.components.ReadinessGauge
import com.example.ui.components.ReleaseCountdownCard
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ReleaseUiStats
import com.example.ui.viewmodel.SongReleaseViewModel

@Composable
fun DashboardScreen(
    viewModel: SongReleaseViewModel,
    currentRelease: SongRelease?,
    allReleases: List<SongRelease>,
    stats: ReleaseUiStats,
    checklist: List<ReleaseChecklistItem>,
    onNavigateToChecklist: () -> Unit,
    onNavigateToMetadata: () -> Unit,
    onNavigateToMarketing: () -> Unit,
    onNavigateToDropLive: () -> Unit,
    onNavigateToSongs: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showNewReleaseDialog by remember { mutableStateOf(false) }

    if (showNewReleaseDialog) {
        NewReleaseDialog(
            onDismiss = { showNewReleaseDialog = false },
            onCreateRelease = { title, artist, featured, genre, subGenre, dateMillis, bpm, key, dist, art, pitch ->
                viewModel.createNewRelease(title, artist, featured, genre, subGenre, dateMillis, bpm, key, dist, art, pitch)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // App Title & Releases Switcher
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "SONG RELEASE",
                            color = HyperCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(MintGreen)
                        )
                    }
                    Text(
                        text = "Artist Launchpad",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { showNewReleaseDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricViolet,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("button_new_release")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Release",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Drop", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Releases horizontal selector chips
            if (allReleases.size > 1) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(allReleases) { rel ->
                        val isSelected = rel.id == currentRelease?.id
                        val coverRes = when (rel.coverArtPreset) {
                            "acoustic" -> R.drawable.img_release_cover_acoustic
                            "studio" -> R.drawable.img_studio_hero
                            else -> R.drawable.img_release_cover_neon
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) ElectricViolet.copy(alpha = 0.25f) else StudioSurfaceCard,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) HyperCyan else StudioBorder
                            ),
                            modifier = Modifier
                                .clickable { viewModel.selectRelease(rel.id) }
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = coverRes),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = rel.title,
                                    color = if (isSelected) TextPrimary else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        if (currentRelease != null) {
            // 1. Release Countdown Banner Card
            item {
                ReleaseCountdownCard(
                    release = currentRelease,
                    stats = stats,
                    onCardClick = onNavigateToDropLive
                )
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 2. Action Hub Quick Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionCard(
                        title = "Checklist",
                        subtitle = "${stats.completedChecklistCount}/${stats.totalChecklistCount} Tasks",
                        icon = Icons.Default.FormatListBulleted,
                        accentColor = ElectricViolet,
                        onClick = onNavigateToChecklist,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Splits & Info",
                        subtitle = "${"%.0f".format(stats.splitTotalPercentage)}% Splits",
                        icon = Icons.Default.VerifiedUser,
                        accentColor = if (stats.isSplitSheetValid) MintGreen else AmberWarning,
                        onClick = onNavigateToMetadata,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionCard(
                        title = "Marketing",
                        subtitle = "${stats.promoCompletedCount}/${stats.promoTotalCount} Promos",
                        icon = Icons.Default.Campaign,
                        accentColor = HyperCyan,
                        onClick = onNavigateToMarketing,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Saved Songs Database Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSongs() }
                        .testTag("card_saved_songs_catalog")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(ElectricViolet.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LibraryMusic,
                                    contentDescription = null,
                                    tint = ElectricViolet,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Room Database Catalog",
                                    color = HyperCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Browse Saved Songs (${allReleases.size})",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "View full song library, titles & release dates",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "View Saved Songs",
                            tint = HyperCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 3. Drop Readiness Gauge
            item {
                ReadinessGauge(stats = stats, checklist = checklist)
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 4. Audio Specs & Waveform
            item {
                AudioWaveformBar(release = currentRelease)
                Spacer(modifier = Modifier.height(14.dp))
            }

            // 5. Urgent Pending Tasks
            item {
                val pendingTasks = checklist.filter { !it.isCompleted }.take(3)
                if (pendingTasks.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Priority Release Tasks",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "View All",
                                    color = HyperCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.clickable { onNavigateToChecklist() }
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            pendingTasks.forEach { task ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = StudioSurfaceHover,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { viewModel.toggleChecklistItem(task) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .border(2.dp, ElectricViolet, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = task.title,
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = task.description,
                                                color = TextSecondary,
                                                fontSize = 11.sp,
                                                maxLines = 1
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
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StudioSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}
