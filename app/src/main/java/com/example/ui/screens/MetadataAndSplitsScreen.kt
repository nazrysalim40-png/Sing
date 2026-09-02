package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Share
import com.example.data.model.SongRelease
import com.example.data.model.SongWriterSplit
import com.example.ui.components.ExportMetadataDialog
import com.example.ui.components.GenreMultiSelectChips
import com.example.ui.components.SongSplitSheetForm
import com.example.ui.components.SplitSheetEditorDialog
import com.example.ui.components.TrackMetadataForm
import com.example.ui.components.TrackMetadataFormData
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
import com.example.ui.viewmodel.SongReleaseViewModel

@Composable
fun MetadataAndSplitsScreen(
    viewModel: SongReleaseViewModel,
    currentRelease: SongRelease?,
    splits: List<SongWriterSplit>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    if (currentRelease == null) return

    var title by remember(currentRelease) { mutableStateOf(currentRelease.title) }
    var artist by remember(currentRelease) { mutableStateOf(currentRelease.artistName) }
    var featured by remember(currentRelease) { mutableStateOf(currentRelease.featuredArtists) }
    var selectedGenres by remember(currentRelease) {
        mutableStateOf(currentRelease.genreList.toSet().ifEmpty { setOf("Pop") })
    }
    var genre by remember(currentRelease) { mutableStateOf(currentRelease.genre) }
    var subGenre by remember(currentRelease) { mutableStateOf(currentRelease.subGenre) }
    var bpmText by remember(currentRelease) { mutableStateOf(currentRelease.bpm.toString()) }
    var musicalKey by remember(currentRelease) { mutableStateOf(currentRelease.musicalKey) }
    var isrcCode by remember(currentRelease) { mutableStateOf(currentRelease.isrcCode) }
    var upcCode by remember(currentRelease) { mutableStateOf(currentRelease.upcCode) }
    var distributor by remember(currentRelease) { mutableStateOf(currentRelease.distributor) }
    var explicitLyrics by remember(currentRelease) { mutableStateOf(currentRelease.explicitLyrics) }
    var preSaveUrl by remember(currentRelease) { mutableStateOf(currentRelease.preSaveUrl) }
    var dspSpotifyUrl by remember(currentRelease) { mutableStateOf(currentRelease.dspSpotifyUrl) }
    var dspAppleMusicUrl by remember(currentRelease) { mutableStateOf(currentRelease.dspAppleMusicUrl) }
    var lyrics by remember(currentRelease) { mutableStateOf(currentRelease.lyrics) }
    var pitchBlurb by remember(currentRelease) { mutableStateOf(currentRelease.pitchBlurb) }

    var showSplitSheetDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    val totalPercentage = splits.sumOf { it.percentage }
    val isSplitValid = Math.abs(totalPercentage - 100.0) < 0.01

    if (showExportDialog) {
        val bpmVal = bpmText.toIntOrNull() ?: 120
        ExportMetadataDialog(
            onDismiss = { showExportDialog = false },
            title = title,
            artist = artist,
            featuredArtists = featured,
            isrc = isrcCode,
            upc = upcCode,
            releaseDateMillis = currentRelease.releaseDateMillis,
            genre = genre,
            subGenre = subGenre,
            bpm = bpmVal,
            musicalKey = musicalKey,
            explicit = explicitLyrics,
            distributor = distributor,
            splits = splits,
            preSaveUrl = preSaveUrl,
            pitchBlurb = pitchBlurb
        )
    }

    if (showSplitSheetDialog) {
        SplitSheetEditorDialog(
            songTitle = title,
            artistName = artist,
            splits = splits,
            onSaveSplits = { updatedSplits ->
                viewModel.saveSplitSheet(updatedSplits)
                Toast.makeText(context, "Split sheet updated & saved!", Toast.LENGTH_SHORT).show()
            },
            onExportClicked = {
                showSplitSheetDialog = false
                showExportDialog = true
            },
            onDismiss = { showSplitSheetDialog = false }
        )
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
                        text = "METADATA & ROYALTY SPLITS",
                        color = HyperCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Track Information",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { showExportDialog = true },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HyperCyan),
                        border = androidx.compose.foundation.BorderStroke(1.dp, HyperCyan.copy(alpha = 0.6f)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("button_export_metadata_screen")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export Metadata",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val bpmVal = bpmText.toIntOrNull() ?: 120
                            val finalGenre = if (selectedGenres.isNotEmpty()) {
                                selectedGenres.joinToString(", ")
                            } else {
                                genre.ifBlank { "Pop" }
                            }
                            viewModel.updateReleaseMetadata(
                                title = title,
                                artistName = artist,
                                featuredArtists = featured,
                                genre = finalGenre,
                                subGenre = subGenre,
                                bpm = bpmVal,
                                musicalKey = musicalKey,
                                isrcCode = isrcCode,
                                upcCode = upcCode,
                                distributor = distributor,
                                releaseDateMillis = currentRelease.releaseDateMillis,
                                explicitLyrics = explicitLyrics,
                                preSaveUrl = preSaveUrl,
                                dspSpotifyUrl = dspSpotifyUrl,
                                dspAppleMusicUrl = dspAppleMusicUrl,
                                lyrics = lyrics,
                                pitchBlurb = pitchBlurb,
                                coverArtPreset = currentRelease.coverArtPreset
                            )
                            Toast.makeText(context, "Track metadata updated!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricViolet,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("button_save_metadata")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Essential Metadata Form Component
        item {
            TrackMetadataForm(
                initialTitle = currentRelease.title,
                initialIsrc = currentRelease.isrcCode,
                initialReleaseDateMillis = currentRelease.releaseDateMillis,
                initialSongwriters = splits,
                onSave = { formData ->
                    viewModel.saveTrackMetadataFormData(
                        title = formData.title,
                        isrcCode = formData.isrcCode,
                        releaseDateMillis = formData.releaseDateMillis,
                        songwriterCredits = formData.songwriterCredits
                    )
                    Toast.makeText(context, "Essential track metadata saved!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Section Title for Extended Distribution & Audio Specs
        item {
            Text(
                text = "EXTENDED DISTRIBUTION & CURATOR SPECS",
                color = HyperCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 1. Songwriter Split Sheet Data Entry Form
        item {
            SongSplitSheetForm(
                songTitle = title,
                artistName = artist,
                initialSplits = splits,
                onSaveSplits = { updatedSplits ->
                    viewModel.saveSplitSheet(updatedSplits)
                    Toast.makeText(context, "Split sheet saved successfully!", Toast.LENGTH_SHORT).show()
                },
                onExportClicked = { showExportDialog = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2. Track Identity & Tags
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Track Identity",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Track Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = artist,
                            onValueChange = { artist = it },
                            label = { Text("Primary Artist") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = featured,
                            onValueChange = { featured = it },
                            label = { Text("Featured Artists") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multi-Select Genre Chips
                    GenreMultiSelectChips(
                        selectedGenres = selectedGenres,
                        onGenresChanged = { selectedGenres = it },
                        title = "Categorized Genres & Tags",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = subGenre,
                        onValueChange = { subGenre = it },
                        label = { Text("Sub-Genre / Style") },
                        placeholder = { Text("e.g. Synthwave, Hyperpop, Dream Pop") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Explicit Switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Explicit Content Flag",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Adds the 'E' badge on streaming platforms",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Switch(
                            checked = explicitLyrics,
                            onCheckedChange = { explicitLyrics = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonPink,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = StudioSurfaceHover
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 3. Audio Specs & Legal Codes
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Codes & Audio Parameters",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = bpmText,
                            onValueChange = { bpmText = it },
                            label = { Text("Tempo (BPM)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = musicalKey,
                            onValueChange = { musicalKey = it },
                            label = { Text("Musical Key") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = isrcCode,
                            onValueChange = { isrcCode = it },
                            label = { Text("ISRC Code") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = upcCode,
                            onValueChange = { upcCode = it },
                            label = { Text("UPC / Barcode") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = distributor,
                        onValueChange = { distributor = it },
                        label = { Text("Distributor Partner") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 4. Lyrics & Editorial Pitch
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Curator Pitch & Song Lyrics",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = pitchBlurb,
                        onValueChange = { pitchBlurb = it },
                        label = { Text("Spotify Editorial Pitch Story") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = lyrics,
                        onValueChange = { lyrics = it },
                        label = { Text("Song Lyrics") },
                        placeholder = { Text("[Verse 1]\nNeon lights reflect the rain...\n\n[Chorus]\nMidnight echoes in the dark...") },
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
