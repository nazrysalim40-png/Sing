package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PressKitGeneratorDialog(
    release: SongRelease,
    splits: List<SongWriterSplit>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTemplate by remember { mutableStateOf(0) } // 0 = Spotify Editorial, 1 = Blog / Press, 2 = Radio / DJ

    val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
    val dropDateStr = dateFormat.format(Date(release.releaseDateMillis))

    val generatedText = when (selectedTemplate) {
        0 -> """
            🎵 SPOTIFY EDITORIAL PLAYLIST PITCH
            
            Track Title: ${release.title}
            Artist: ${release.artistName} ${if (release.featuredArtists.isNotBlank()) "(${release.featuredArtists})" else ""}
            Release Date: $dropDateStr
            Genre: ${release.genre} / ${release.subGenre}
            BPM: ${release.bpm} | Key: ${release.musicalKey} | Language: English
            Distributor: ${release.distributor}
            
            STORY & CURATOR PITCH:
            ${release.pitchBlurb}
            
            PRODUCTION DETAILS:
            Recorded in 24-bit / 48kHz WAV audio, master loudness normalized to -14 LUFS standard. Includes 9:16 vertical looping Canvas asset.
            
            TARGET PLAYLIST MOODS:
            Late Night Drives, Workout Energy, Synthpop Vibes, Indie Discoveries.
        """.trimIndent()

        1 -> """
            📰 PRESS RELEASE & BLOG OUTREACH (EPK)
            
            FOR IMMEDIATE RELEASE:
            ${release.artistName} Announces New Single "${release.title}" Dropping $dropDateStr
            
            "${release.title}" is the upcoming ${release.genre} anthem from ${release.artistName}. 
            ${release.pitchBlurb}
            
            TRACK CREDITS & METADATA:
            • Song Title: ${release.title}
            • Primary Artist: ${release.artistName}
            • ISRC: ${release.isrcCode.ifBlank { "Pending Ingestion" }}
            • UPC: ${release.upcCode.ifBlank { "Pending Distributor" }}
            • Written & Produced by: ${splits.joinToString(", ") { "${it.contributorName} (${it.role})" }}
            • Pre-Save Smart Link: ${release.preSaveUrl}
            
            Press Contact & Inquiries:
            management@${release.artistName.lowercase().replace(" ", "")}music.com
        """.trimIndent()

        else -> """
            📻 RADIO & DJ SUBMISSION SHEET
            
            Track: ${release.title} (Radio Edit / Main Master)
            Artist: ${release.artistName} ${release.featuredArtists}
            Release Date: $dropDateStr
            Length: 3:24 | BPM: ${release.bpm} | Key: ${release.musicalKey}
            Explicit: ${if (release.explicitLyrics) "YES (Clean edit available)" else "NO (Clean/Radio Friendly)"}
            PRO Affiliations: ${splits.joinToString(" / ") { "${it.contributorName} (${it.proAffiliation})" }}
            
            Distributor: ${release.distributor}
            Smart Link: ${release.preSaveUrl}
        """.trimIndent()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
            border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = HyperCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "EPK & Pitch Generator",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Template selector tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val tabs = listOf("Spotify Pitch", "Press / EPK", "Radio / DJ")
                    tabs.forEachIndexed { index, title ->
                        val isSelected = selectedTemplate == index
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) ElectricViolet else StudioBackground,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) ElectricViolet else StudioBorder),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            onClick = { selectedTemplate = index }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = title,
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Output copy container
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0C0A16),
                    border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = generatedText,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("EPK Pitch", generatedText)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Pitch kit copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HyperCyan,
                        contentColor = Color.Black
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Formatted Pitch to Clipboard", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
