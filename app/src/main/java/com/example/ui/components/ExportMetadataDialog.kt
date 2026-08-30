package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Song
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MintGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.MetadataExportUtils

enum class ExportFormat(val title: String) {
    SHARE_SHEET("Delivery Share Sheet"),
    CSV_FORMAT("Distributor CSV")
}

/**
 * Dialog enabling artists to export song metadata to CSV or Share Sheet format for distributor delivery.
 */
@Composable
fun ExportMetadataDialog(
    onDismiss: () -> Unit,
    title: String,
    artist: String,
    featuredArtists: String = "",
    isrc: String = "",
    upc: String = "",
    releaseDateMillis: Long = System.currentTimeMillis(),
    genre: String = "Pop",
    subGenre: String = "",
    bpm: Int = 120,
    musicalKey: String = "C Major",
    explicit: Boolean = false,
    distributor: String = "DistroKid",
    splits: List<SongWriterSplit> = emptyList(),
    preSaveUrl: String = "",
    pitchBlurb: String = "",
    allSongsList: List<Song>? = null
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var exportBatchMode by remember { mutableStateOf(false) }

    val singleShareSheetContent = remember(title, artist, isrc, releaseDateMillis, splits, pitchBlurb) {
        MetadataExportUtils.generateDistributorShareSheet(
            title = title,
            artist = artist,
            featuredArtists = featuredArtists,
            isrc = isrc,
            upc = upc,
            releaseDateMillis = releaseDateMillis,
            genre = genre,
            subGenre = subGenre,
            bpm = bpm,
            musicalKey = musicalKey,
            explicit = explicit,
            distributor = distributor,
            splits = splits,
            preSaveUrl = preSaveUrl,
            pitchBlurb = pitchBlurb
        )
    }

    val singleCsvContent = remember(title, artist, isrc, releaseDateMillis, splits, pitchBlurb) {
        val splitsSummary = splits.joinToString("; ") { "${it.contributorName} (${it.role} ${it.percentage}%)" }
        MetadataExportUtils.exportSongToCsv(
            title = title,
            artist = artist,
            featuredArtists = featuredArtists,
            isrc = isrc,
            upc = upc,
            releaseDateMillis = releaseDateMillis,
            genre = genre,
            subGenre = subGenre,
            bpm = bpm,
            musicalKey = musicalKey,
            explicit = explicit,
            distributor = distributor,
            pitchBlurb = pitchBlurb,
            splitsSummary = splitsSummary
        )
    }

    val batchCsvContent = remember(allSongsList) {
        if (!allSongsList.isNullOrEmpty()) {
            MetadataExportUtils.exportSongsListToCsv(allSongsList)
        } else {
            singleCsvContent
        }
    }

    val activeContent = when {
        selectedTab == 0 -> singleShareSheetContent
        exportBatchMode && !allSongsList.isNullOrEmpty() -> batchCsvContent
        else -> singleCsvContent
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StudioSurfaceCard,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = HyperCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DISTRIBUTOR EXPORT",
                            color = HyperCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                    Text(
                        text = "Export Metadata",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tab Selection (Share Sheet vs CSV Format)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = StudioSurfaceHover,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = HyperCyan
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Description,
                                    contentDescription = null,
                                    tint = if (selectedTab == 0) HyperCyan else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "Share Sheet",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 0) TextPrimary else TextSecondary
                                )
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.TableChart,
                                    contentDescription = null,
                                    tint = if (selectedTab == 1) HyperCyan else TextSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "CSV File",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 1) TextPrimary else TextSecondary
                                )
                            }
                        }
                    )
                }

                // Batch Mode Toggle for CSV (if multiple songs available)
                if (selectedTab == 1 && !allSongsList.isNullOrEmpty() && allSongsList.size > 1) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(StudioSurfaceHover, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = !exportBatchMode,
                            onClick = { exportBatchMode = false },
                            label = { Text("Single Track CSV", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricViolet,
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = exportBatchMode,
                            onClick = { exportBatchMode = true },
                            label = { Text("All ${allSongsList.size} Saved Songs CSV", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricViolet,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Target Explanation
                Text(
                    text = if (selectedTab == 0) {
                        "Formatted delivery sheet ready to share with managers, co-writers, or copy-paste into DistroKid/TuneCore/Spotify for Artists."
                    } else {
                        "Standard RFC 4180 CSV export ready for import into distribution aggregators and catalog tracking sheets."
                    },
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                // Preview Box
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
                    border = BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp, max = 220.dp)
                            .padding(12.dp)
                            .horizontalScroll(rememberScrollState())
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = activeContent,
                            color = if (selectedTab == 0) MintGreen else HyperCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Copy to Clipboard Button
                OutlinedButton(
                    onClick = {
                        val label = if (selectedTab == 0) "Delivery Share Sheet" else "CSV Metadata"
                        MetadataExportUtils.copyToClipboard(context, label, activeContent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HyperCyan),
                    border = BorderStroke(1.dp, HyperCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("button_copy_metadata_export")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Copy", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Share Sheet Button
                Button(
                    onClick = {
                        val mimeType = if (selectedTab == 1) "text/csv" else "text/plain"
                        val subject = if (selectedTab == 1) "$title - Metadata.csv" else "$title - Distributor Delivery Sheet"
                        MetadataExportUtils.shareViaSystemSheet(
                            context = context,
                            content = activeContent,
                            subject = subject,
                            mimeType = mimeType
                        )
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                    modifier = Modifier.testTag("button_share_metadata_export")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (selectedTab == 0) "Share Sheet" else "Export CSV",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close", color = TextSecondary, fontSize = 12.sp)
            }
        }
    )
}
