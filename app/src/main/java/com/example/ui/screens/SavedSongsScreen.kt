package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Share
import com.example.R
import com.example.data.model.Song
import com.example.ui.components.ExportMetadataDialog
import com.example.ui.components.MonthlyReleaseFrequencyChart
import com.example.ui.components.NewReleaseDialog
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MintGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.StudioBackground
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.SongReleaseViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Screen that queries the Room database and displays a list of all saved songs,
 * showing the title and release date for each, along with extended distribution metadata.
 */
@Composable
fun SavedSongsScreen(
    viewModel: SongReleaseViewModel,
    onSongSelected: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allSongs by viewModel.allSongs.collectAsStateWithLifecycle()
    val currentRelease by viewModel.currentRelease.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var showNewSongDialog by remember { mutableStateOf(false) }
    var songToDelete by remember { mutableStateOf<Song?>(null) }
    var songToExport by remember { mutableStateOf<Song?>(null) }
    var showBatchExportDialog by remember { mutableStateOf(false) }
    var expandedSongId by remember { mutableStateOf<Long?>(null) }
    var showFrequencyChart by remember { mutableStateOf(true) }

    val dateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val fullDateFormatter = remember { SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()) }

    // Filtered songs
    val filteredSongs = remember(allSongs, searchQuery, selectedFilter) {
        val now = System.currentTimeMillis()
        allSongs.filter { song ->
            val matchesSearch = searchQuery.isBlank() ||
                song.title.contains(searchQuery, ignoreCase = true) ||
                song.artistName.contains(searchQuery, ignoreCase = true) ||
                song.genre.contains(searchQuery, ignoreCase = true) ||
                song.isrcCode.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Upcoming" -> song.releaseDateMillis > now
                "Released" -> song.releaseDateMillis <= now
                "Pop" -> song.genre.contains("Pop", ignoreCase = true)
                "Electronic" -> song.genre.contains("Electronic", ignoreCase = true) || song.genre.contains("Synth", ignoreCase = true)
                "Rock/Alt" -> song.genre.contains("Rock", ignoreCase = true) || song.genre.contains("Indie", ignoreCase = true)
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    if (showNewSongDialog) {
        NewReleaseDialog(
            onDismiss = { showNewSongDialog = false },
            onCreateRelease = { title, artist, featured, genre, subGenre, dateMillis, bpm, key, dist, art, pitch ->
                viewModel.createNewRelease(title, artist, featured, genre, subGenre, dateMillis, bpm, key, dist, art, pitch)
                Toast.makeText(context, "New song created & stored in Room!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // Delete Confirmation Dialog
    if (songToDelete != null) {
        val song = songToDelete!!
        AlertDialog(
            onDismissRequest = { songToDelete = null },
            containerColor = StudioSurfaceCard,
            title = {
                Text(
                    text = "Delete Saved Song?",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to remove \"${song.title}\" from the Room database? This cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSong(song)
                        songToDelete = null
                        Toast.makeText(context, "Song deleted from database", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPink)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { songToDelete = null }
                ) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Export Dialog (Single Song or Batch Catalog)
    if (songToExport != null) {
        val song = songToExport!!
        ExportMetadataDialog(
            onDismiss = { songToExport = null },
            title = song.title,
            artist = song.artistName,
            isrc = song.isrcCode,
            upc = song.upcCode,
            releaseDateMillis = song.releaseDateMillis,
            genre = song.genre,
            subGenre = song.subGenre,
            bpm = song.bpm,
            musicalKey = song.musicalKey,
            explicit = song.explicitLyrics,
            distributor = song.distributor,
            pitchBlurb = song.pitchBlurb,
            allSongsList = allSongs
        )
    } else if (showBatchExportDialog && allSongs.isNotEmpty()) {
        val primary = allSongs.first()
        ExportMetadataDialog(
            onDismiss = { showBatchExportDialog = false },
            title = primary.title,
            artist = primary.artistName,
            isrc = primary.isrcCode,
            upc = primary.upcCode,
            releaseDateMillis = primary.releaseDateMillis,
            genre = primary.genre,
            subGenre = primary.subGenre,
            bpm = primary.bpm,
            musicalKey = primary.musicalKey,
            explicit = primary.explicitLyrics,
            distributor = primary.distributor,
            pitchBlurb = primary.pitchBlurb,
            allSongsList = allSongs
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(StudioBackground)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = HyperCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ROOM DATABASE CATALOG",
                                color = HyperCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                        }
                        Text(
                            text = "Saved Songs",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Export All CSV / Share Sheet Button
                        if (allSongs.isNotEmpty()) {
                            OutlinedButton(
                                onClick = { showBatchExportDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = HyperCyan),
                                border = BorderStroke(1.dp, HyperCyan.copy(alpha = 0.6f)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("button_export_all_songs_csv")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Export CSV",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Count Badge
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ElectricViolet.copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LibraryMusic,
                                    contentDescription = null,
                                    tint = ElectricViolet,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${allSongs.size} Tracks",
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Monthly Release Frequency Chart Visualization
            item {
                MonthlyReleaseFrequencyChart(
                    songs = allSongs,
                    onAddReleaseForMonth = { year, monthIndex ->
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.YEAR, year)
                            set(Calendar.MONTH, monthIndex)
                            set(Calendar.DAY_OF_MONTH, 15)
                        }
                        showNewSongDialog = true
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Search Bar at Top of Song List Screen
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                "Filter songs by title...",
                                color = TextMuted,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search songs by title",
                                tint = if (searchQuery.isNotBlank()) HyperCyan else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" },
                                    modifier = Modifier.testTag("button_clear_search")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search",
                                        tint = HyperCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HyperCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = StudioSurfaceHover,
                            unfocusedContainerColor = StudioSurfaceCard
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_search_songs")
                    )

                    // Active Search Info / Filter Status
                    if (searchQuery.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Matching titles for \"$searchQuery\": ${filteredSongs.size} found",
                                color = HyperCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Clear",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { searchQuery = "" }
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            }

            // Quick Filters
            item {
                val filters = listOf("All", "Upcoming", "Released", "Pop", "Electronic", "Rock/Alt")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricViolet,
                                selectedLabelColor = Color.White,
                                containerColor = StudioSurfaceCard,
                                labelColor = TextSecondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) HyperCyan else StudioBorder
                            )
                        )
                    }
                }
            }

            // Songs List
            if (filteredSongs.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                        border = BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp)
                            .testTag("empty_saved_songs_card")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(ElectricViolet.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = ElectricViolet,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = if (searchQuery.isNotEmpty()) "No matching songs found" else "No saved songs yet",
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (searchQuery.isNotEmpty()) "Try a different search query or clear the filter." else "Create your first release using the track metadata form to store it in Room.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(top = 6.dp)
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { showNewSongDialog = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                modifier = Modifier.testTag("button_empty_add_song")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add New Song Release", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                items(filteredSongs, key = { it.id }) { song ->
                    val isExpanded = expandedSongId == song.id
                    val isCurrentActive = song.releaseId != null && song.releaseId == currentRelease?.id
                    val daysDiff = remember(song.releaseDateMillis) {
                        val diff = song.releaseDateMillis - System.currentTimeMillis()
                        if (diff > 0) diff / (1000 * 60 * 60 * 24) else -1
                    }

                    val coverRes = when (song.coverArtPreset) {
                        "acoustic" -> R.drawable.img_release_cover_acoustic
                        "studio" -> R.drawable.img_studio_hero
                        else -> R.drawable.img_release_cover_neon
                    }

                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrentActive) StudioSurfaceCard else StudioSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isCurrentActive) ElectricViolet else StudioBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("song_item_${song.id}")
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Main Card Content (Title & Release Date)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        expandedSongId = if (isExpanded) null else song.id
                                    }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Cover Thumbnail
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(StudioSurfaceHover)
                                ) {
                                    Image(
                                        painter = painterResource(id = coverRes),
                                        contentDescription = "Cover Art",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    if (isCurrentActive) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(3.dp)
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(MintGreen)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Song Title & Core Details
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = song.title,
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        if (isCurrentActive) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = MintGreen.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = "ACTIVE",
                                                    color = MintGreen,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Release Date Row
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = "Release Date",
                                            tint = HyperCyan,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Release Date: ${dateFormatter.format(Date(song.releaseDateMillis))}",
                                            color = HyperCyan,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    // Artist & Genre
                                    Text(
                                        text = "${song.artistName.ifBlank { "Independent Artist" }} • ${song.genre}",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Drop Status Pill
                                Column(
                                    horizontalAlignment = Alignment.End,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (daysDiff >= 0) ElectricViolet.copy(alpha = 0.2f) else MintGreen.copy(alpha = 0.2f),
                                        border = BorderStroke(
                                            1.dp,
                                            if (daysDiff >= 0) ElectricViolet.copy(alpha = 0.4f) else MintGreen.copy(alpha = 0.4f)
                                        )
                                    ) {
                                        Text(
                                            text = if (daysDiff > 0) "In $daysDiff Days" else if (daysDiff == 0L) "Today" else "Live",
                                            color = if (daysDiff >= 0) ElectricViolet else MintGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { expandedSongId = if (isExpanded) null else song.id },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = "Toggle details",
                                            tint = TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // Expanded Details Section
                            AnimatedVisibility(
                                visible = isExpanded,
                                enter = fadeIn(),
                                exit = fadeOut()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(StudioSurfaceHover)
                                        .padding(16.dp)
                                ) {
                                    Divider(color = StudioBorder, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Text(
                                        text = "ROOM DATABASE METADATA",
                                        color = HyperCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Metadata Grid
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            MetadataSpecRow(label = "Full Release Date", value = fullDateFormatter.format(Date(song.releaseDateMillis)))
                                            MetadataSpecRow(label = "ISRC Code", value = song.isrcCode.ifBlank { "Not Assigned" })
                                            MetadataSpecRow(label = "Tempo / BPM", value = "${song.bpm} BPM")
                                            MetadataSpecRow(label = "Musical Key", value = song.musicalKey)
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            MetadataSpecRow(label = "Distributor", value = song.distributor)
                                            MetadataSpecRow(label = "Sub-Genre", value = song.subGenre)
                                            MetadataSpecRow(label = "Explicit", value = if (song.explicitLyrics) "Yes (Explicit)" else "Clean (Safe)")
                                            MetadataSpecRow(label = "Room Record ID", value = "#${song.id}")
                                        }
                                    }

                                    if (song.pitchBlurb.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Pitch / Curator Blurb:",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = song.pitchBlurb,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Action Buttons
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (song.releaseId != null) {
                                            Button(
                                                onClick = {
                                                    viewModel.selectRelease(song.releaseId)
                                                    onSongSelected(song.releaseId)
                                                    Toast.makeText(context, "Switched active project to \"${song.title}\"", Toast.LENGTH_SHORT).show()
                                                },
                                                shape = RoundedCornerShape(10.dp),
                                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Workspace", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        // Export Button
                                        OutlinedButton(
                                            onClick = { songToExport = song },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HyperCyan),
                                            border = BorderStroke(1.dp, HyperCyan.copy(alpha = 0.6f)),
                                            modifier = Modifier.testTag("button_export_song_${song.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Export Metadata",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Export", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }

                                        OutlinedButton(
                                            onClick = { songToDelete = song },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonPink),
                                            border = BorderStroke(1.dp, NeonPink.copy(alpha = 0.5f))
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button to Add Song
        FloatingActionButton(
            onClick = { showNewSongDialog = true },
            containerColor = ElectricViolet,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
                .testTag("fab_add_song")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add New Song",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun MetadataSpecRow(
    label: String,
    value: String
) {
    Column(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
