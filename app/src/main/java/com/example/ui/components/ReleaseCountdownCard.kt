package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ReleaseStatus
import com.example.data.model.SongRelease
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ReleaseUiStats
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReleaseCountdownCard(
    release: SongRelease,
    stats: ReleaseUiStats,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotate")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "vinyl_angle"
    )

    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(release.releaseDateMillis))

    val coverArtRes = when (release.coverArtPreset) {
        "acoustic" -> R.drawable.img_release_cover_acoustic
        "studio" -> R.drawable.img_studio_hero
        else -> R.drawable.img_release_cover_neon
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            Brush.horizontalGradient(listOf(ElectricViolet.copy(alpha = 0.5f), HyperCyan.copy(alpha = 0.4f)))
        ),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("release_countdown_card")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            ElectricViolet.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Status Header Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val (statusBg, statusColor, statusLabel) = when (release.status) {
                        ReleaseStatus.DROPPED -> Triple(MintGreen.copy(alpha = 0.2f), MintGreen, "LIVE ON DSPs")
                        ReleaseStatus.PRE_SAVE_ACTIVE -> Triple(HyperCyan.copy(alpha = 0.2f), HyperCyan, "PRE-SAVE ACTIVE")
                        ReleaseStatus.SCHEDULED -> Triple(ElectricViolet.copy(alpha = 0.25f), ElectricViolet, "DISTRIBUTION SCHEDULED")
                        ReleaseStatus.MIX_MASTER -> Triple(AmberWarning.copy(alpha = 0.2f), AmberWarning, "MIX & MASTER")
                        else -> Triple(StudioBorder, TextSecondary, release.status.displayName.uppercase())
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = statusBg,
                        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.6f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(statusColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = statusLabel,
                                color = statusColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Text(
                        text = formattedDate,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Artwork & Title Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Spinning vinyl record illusion behind album cover
                    Box(
                        modifier = Modifier.size(88.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Vinyl disc background
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .rotate(if (!stats.isDropped) rotationAngle else 0f)
                                .clip(CircleShape)
                                .background(Color(0xFF0F0D1A))
                                .border(1.dp, StudioBorder, CircleShape)
                        )

                        // Album artwork square
                        Image(
                            painter = painterResource(id = coverArtRes),
                            contentDescription = "Release Artwork",
                            modifier = Modifier
                                .size(76.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, ElectricViolet.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = release.title,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (release.featuredArtists.isNotBlank()) "${release.artistName} ${release.featuredArtists}" else release.artistName,
                            color = HyperCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${release.genre} • ${release.subGenre}",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Countdown Numbers or Drop Celebration
                if (stats.isDropped) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MintGreen.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MintGreen.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Celebration,
                                contentDescription = "Dropped",
                                tint = MintGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Track is Officially Dropped & Streaming Worldwide!",
                                color = MintGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        CountdownUnit(
                            value = stats.daysUntilRelease.toString().padStart(2, '0'),
                            label = "DAYS",
                            accentColor = ElectricViolet
                        )
                        CountdownDivider()
                        CountdownUnit(
                            value = stats.hoursRemaining.toString().padStart(2, '0'),
                            label = "HOURS",
                            accentColor = HyperCyan
                        )
                        CountdownDivider()
                        CountdownUnit(
                            value = "${stats.completionPercentage}%",
                            label = "READY",
                            accentColor = if (stats.completionPercentage >= 80) MintGreen else AmberWarning
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownUnit(
    value: String,
    label: String,
    accentColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = StudioBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(76.dp)
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = value,
                    color = accentColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun CountdownDivider() {
    Box(
        modifier = Modifier
            .height(48.dp)
            .padding(top = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ":",
            color = TextSecondary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
