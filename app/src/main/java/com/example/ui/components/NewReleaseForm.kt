package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ReleaseStatus
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val PopularDistributors = listOf(
    "DistroKid",
    "TuneCore",
    "CD Baby",
    "Amuse",
    "Ditto",
    "AWAL"
)

val PopularGenres = listOf(
    "Pop",
    "Electronic / Synth",
    "Indie Rock",
    "Hip Hop / R&B",
    "Acoustic / Folk",
    "Alternative"
)

/**
 * Data entry form UI component to add a new music release to the Room database.
 * Includes fields for title, artist, distribution date (with interactive date picker
 * and lead-time presets), distributor, genre, audio parameters, and cover art style.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewReleaseForm(
    onSaveRelease: (
        title: String,
        artist: String,
        featured: String,
        genre: String,
        subGenre: String,
        releaseDateMillis: Long,
        bpm: Int,
        key: String,
        distributor: String,
        coverArtPreset: String,
        pitchBlurb: String
    ) -> Unit,
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    // Core Fields
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("Astraea") }
    var featured by remember { mutableStateOf("") }

    // Distribution Date: defaults to 3 weeks ahead (standard DSP pitch window)
    val defaultReleaseTime = remember {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 21)
        cal.timeInMillis
    }
    var distributionDateMillis by remember { mutableLongStateOf(defaultReleaseTime) }

    // Detailed Music Metadata
    var selectedGenres by remember { mutableStateOf(setOf("Pop")) }
    var subGenre by remember { mutableStateOf("Synthpop") }
    var bpmText by remember { mutableStateOf("124") }
    var musicalKey by remember { mutableStateOf("F# Minor") }
    var distributor by remember { mutableStateOf("DistroKid") }
    var coverArtPreset by remember { mutableStateOf("neon") }
    var pitchBlurb by remember { mutableStateOf("") }

    // Advanced fields expandable toggle
    var showAdvancedDetails by remember { mutableStateOf(false) }

    // Formatters
    val fullDateFormatter = remember { SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault()) }
    val shortDateFormatter = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }

    // Date calculations
    val now = System.currentTimeMillis()
    val daysUntilDrop = ((distributionDateMillis - now) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)

    val isTitleValid = title.isNotBlank()
    val isArtistValid = artist.isNotBlank()
    val isFormValid = isTitleValid && isArtistValid

    // Helper to open Android DatePickerDialog
    fun openDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = distributionDateMillis }
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                distributionDateMillis = selectedCal.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        // Minimum date today
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = BorderStroke(1.dp, StudioBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
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
                            .background(ElectricViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.RocketLaunch,
                            contentDescription = null,
                            tint = HyperCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "DATABASE ENTRY FORM",
                            color = HyperCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Add Music Release",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (onCancel != null) {
                    IconButton(
                        onClick = onCancel,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION 1: Essential Identity (Title & Artist)
            Text(
                text = "TRACK IDENTITY",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // 1. Song Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Song Title *") },
                placeholder = { Text("e.g. Starlight Velocity, Midnight Echoes") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (isTitleValid) HyperCyan else TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HyperCyan,
                    unfocusedBorderColor = StudioBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = StudioSurfaceHover,
                    unfocusedContainerColor = StudioSurfaceHover
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_song_title")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Primary Artist & Featured Artists Fields
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Primary Artist *") },
                    placeholder = { Text("Artist / Band") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = if (isArtistValid) ElectricViolet else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = StudioSurfaceHover,
                        unfocusedContainerColor = StudioSurfaceHover
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_artist_name")
                )

                OutlinedTextField(
                    value = featured,
                    onValueChange = { featured = it },
                    label = { Text("Featured Artists") },
                    placeholder = { Text("feat. Artist") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = StudioSurfaceHover,
                        unfocusedContainerColor = StudioSurfaceHover
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_featured_artists")
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 2: Distribution Date & Pitch Timeline
            Text(
                text = "DISTRIBUTION & RELEASE DATE",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Distribution Date Card with Calendar Picker
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = StudioSurfaceHover,
                border = BorderStroke(1.dp, StudioBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { openDatePicker() }
                    .testTag("button_pick_distribution_date")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(HyperCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "Pick Date",
                                tint = HyperCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Distribution Drop Date",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = fullDateFormatter.format(Date(distributionDateMillis)),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MintGreen.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = if (daysUntilDrop > 0) "In $daysUntilDrop Days" else "Today",
                            color = MintGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Lead-Time Preset Chips (2w, 3w, 4w, 6w, 8w)
            Text(
                text = "Quick Lead-Time Presets:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val leadPresets = listOf(
                    14L to "2 Weeks",
                    21L to "3 Weeks (Pitch)",
                    28L to "4 Weeks",
                    42L to "6 Weeks"
                )

                leadPresets.forEach { (days, label) ->
                    val isSelected = daysUntilDrop in (days - 1)..(days + 1)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) ElectricViolet.copy(alpha = 0.25f) else StudioSurfaceHover,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) ElectricViolet else StudioBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val cal = Calendar.getInstance().apply {
                                    add(Calendar.DAY_OF_YEAR, days.toInt())
                                }
                                distributionDateMillis = cal.timeInMillis
                            }
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) HyperCyan else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .align(Alignment.CenterVertically),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SECTION 3: Distributor & Multi-Select Genre
            OutlinedTextField(
                value = distributor,
                onValueChange = { distributor = it },
                label = { Text("Digital Distributor") },
                placeholder = { Text("DistroKid / TuneCore / CD Baby") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = HyperCyan,
                        modifier = Modifier.size(16.dp)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = HyperCyan,
                    unfocusedBorderColor = StudioBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = StudioSurfaceHover,
                    unfocusedContainerColor = StudioSurfaceHover
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_distributor")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Select Genre Chip UI Component
            GenreMultiSelectChips(
                selectedGenres = selectedGenres,
                onGenresChanged = { selectedGenres = it },
                title = "Release Genre Categorization",
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_genre")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Toggle Advanced Metadata
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAdvancedDetails = !showAdvancedDetails }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = HyperCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (showAdvancedDetails) "Hide Technical Audio & Art Details" else "Add Audio Details & Cover Art Style",
                        color = HyperCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (showAdvancedDetails) "▲" else "▼",
                    color = HyperCyan,
                    fontSize = 12.sp
                )
            }

            AnimatedVisibility(visible = showAdvancedDetails) {
                Column(modifier = Modifier.padding(top = 8.dp)) {
                    // Sub-genre & Key & BPM
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = subGenre,
                            onValueChange = { subGenre = it },
                            label = { Text("Sub-Genre") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioSurfaceHover,
                                unfocusedContainerColor = StudioSurfaceHover
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = bpmText,
                            onValueChange = { bpmText = it },
                            label = { Text("BPM") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioSurfaceHover,
                                unfocusedContainerColor = StudioSurfaceHover
                            ),
                            modifier = Modifier.weight(0.7f)
                        )

                        OutlinedTextField(
                            value = musicalKey,
                            onValueChange = { musicalKey = it },
                            label = { Text("Key") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricViolet,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioSurfaceHover,
                                unfocusedContainerColor = StudioSurfaceHover
                            ),
                            modifier = Modifier.weight(0.9f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Cover Art Preset
                    Text(
                        text = "Cover Artwork Theme",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val presets = listOf(
                            Triple("neon", R.drawable.img_release_cover_neon, "Neon Synth"),
                            Triple("acoustic", R.drawable.img_release_cover_acoustic, "Warm Folk"),
                            Triple("light", R.drawable.img_release_cover_light, "Light Mood"),
                            Triple("studio", R.drawable.img_studio_hero, "Studio Pro")
                        )

                        presets.forEach { (key, res, label) ->
                            val isSelected = coverArtPreset == key
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) ElectricViolet.copy(alpha = 0.2f) else StudioSurfaceHover,
                                border = BorderStroke(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) HyperCyan else StudioBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { coverArtPreset = key }
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        painter = painterResource(id = res),
                                        contentDescription = label,
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(8.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = label,
                                        color = if (isSelected) HyperCyan else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pitch Blurb
                    OutlinedTextField(
                        value = pitchBlurb,
                        onValueChange = { pitchBlurb = it },
                        label = { Text("Spotify Editorial Pitch Story (Optional)") },
                        placeholder = { Text("What inspired this song? Describe the vibe, instrumentation, and target playlists.") },
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricViolet,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = StudioSurfaceHover,
                            unfocusedContainerColor = StudioSurfaceHover
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SECTION 4: Action Button
            Button(
                onClick = {
                    if (isFormValid) {
                        val bpmVal = bpmText.toIntOrNull() ?: 120
                        val finalGenre = if (selectedGenres.isNotEmpty()) {
                            selectedGenres.joinToString(", ")
                        } else {
                            "Pop"
                        }
                        onSaveRelease(
                            title.trim(),
                            artist.trim(),
                            featured.trim(),
                            finalGenre,
                            subGenre.trim(),
                            distributionDateMillis,
                            bpmVal,
                            musicalKey.trim(),
                            distributor.trim(),
                            coverArtPreset,
                            pitchBlurb.trim()
                        )
                    }
                },
                enabled = isFormValid,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricViolet,
                    contentColor = Color.White,
                    disabledContainerColor = StudioSurfaceHover,
                    disabledContentColor = TextMuted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("create_release_submit_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Add Release to Database",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
