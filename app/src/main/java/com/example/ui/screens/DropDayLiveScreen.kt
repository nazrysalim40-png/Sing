package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ReleaseUiStats
import com.example.ui.viewmodel.SongReleaseViewModel

@Composable
fun DropDayLiveScreen(
    viewModel: SongReleaseViewModel,
    currentRelease: SongRelease?,
    stats: ReleaseUiStats,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (currentRelease == null) return

    val isDropped = currentRelease.status == ReleaseStatus.DROPPED
    val coverRes = when (currentRelease.coverArtPreset) {
        "acoustic" -> R.drawable.img_release_cover_acoustic
        "studio" -> R.drawable.img_studio_hero
        else -> R.drawable.img_release_cover_neon
    }

    var selectedSocialTab by remember { mutableStateOf(0) } // 0 = Instagram, 1 = TikTok, 2 = Twitter/X

    val socialCopyText = when (selectedSocialTab) {
        0 -> """
            OUT NOW EVERYWHERE 🚨💿
            "${currentRelease.title}" is officially live on all streaming platforms! 
            
            This song was born from late night studio sessions with @nova_soundlab. Stream it, save it, and let me know your favorite lyric in the comments! 
            
            Link in bio to listen on Spotify / Apple Music / YouTube! 🎧✨
            #${currentRelease.title.replace(" ", "")} #NewMusic #OutNow #Synthpop #IndieArtist
        """.trimIndent()

        1 -> """
            MY NEW SONG "${currentRelease.title}" IS FINALLY OUT 😭✨ 
            Tap the sound or link in bio to stream full track on Spotify!
            What visual should I make next?
            #newmusic #musician #behindthescenes #studiosession
        """.trimIndent()

        else -> """
            🚨 OUT NOW: "${currentRelease.title}" is officially live on Spotify, Apple Music & all DSPs worldwide!
            
            Produced by Astraea.
            Listen here: ${currentRelease.preSaveUrl}
            
            RT & share the love! ❤️
        """.trimIndent()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LIVE DROP COMMAND CENTER",
                        color = NeonPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Drop Day Control",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isDropped) MintGreen.copy(alpha = 0.2f) else ElectricViolet.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDropped) MintGreen else ElectricViolet
                    )
                ) {
                    Text(
                        text = if (isDropped) "LIVE DROP" else "STANDBY",
                        color = if (isDropped) MintGreen else ElectricViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 1. Live Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(NeonPink.copy(alpha = 0.6f), HyperCyan.copy(alpha = 0.6f)))
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    if (isDropped) MintGreen.copy(alpha = 0.15f) else NeonPink.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = coverRes),
                        contentDescription = currentRelease.title,
                        modifier = Modifier
                            .size(140.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .border(2.dp, HyperCyan.copy(alpha = 0.7f), RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentRelease.title,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = currentRelease.artistName,
                        color = HyperCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isDropped) {
                        Button(
                            onClick = {
                                viewModel.triggerSimulatedDrop()
                                Toast.makeText(context, "Track dropped successfully! Live streaming links activated.", Toast.LENGTH_LONG).show()
                            },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonPink,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("button_trigger_drop")
                        ) {
                            Icon(imageVector = Icons.Default.RocketLaunch, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GO LIVE: Trigger Release Drop", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MintGreen.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MintGreen)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = MintGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Track is Officially Streaming Worldwide",
                                    color = MintGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Direct Streaming Ingestion Links
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Global DSP Streaming Portals",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val dsps = listOf(
                        Triple("Spotify", Color(0xFF1DB954), "spotify:track:release_${currentRelease.id}"),
                        Triple("Apple Music", Color(0xFFFA243C), "https://music.apple.com"),
                        Triple("YouTube Music", Color(0xFFFF0000), "https://music.youtube.com"),
                        Triple("Amazon Music", Color(0xFF00A8E1), "https://music.amazon.com"),
                        Triple("Tidal (Master)", Color(0xFF00FFFF), "https://tidal.com")
                    )

                    dsps.forEach { (name, color, link) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = StudioSurfaceHover,
                            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    Toast.makeText(context, "Opening $name streaming portal...", Toast.LENGTH_SHORT).show()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = name,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isDropped) "LIVE 🟢" else "READY",
                                        color = if (isDropped) MintGreen else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Launch,
                                        contentDescription = null,
                                        tint = HyperCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Social Media Blast Generator
        item {
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
                            text = "Release Day Social Blast Copy",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Social Blast", socialCopyText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Announcement copy copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = HyperCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Social Tab Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val tabs = listOf("Instagram", "TikTok", "X / Twitter")
                        tabs.forEachIndexed { idx, label ->
                            val isSel = selectedSocialTab == idx
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) ElectricViolet else StudioBackground,
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ElectricViolet else StudioBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .clickable { selectedSocialTab = idx }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = label,
                                        color = if (isSel) Color.White else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0C0A16),
                        border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = socialCopyText,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Social Blast", socialCopyText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Announcement copy copied to clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricViolet,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Caption for Posting", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
