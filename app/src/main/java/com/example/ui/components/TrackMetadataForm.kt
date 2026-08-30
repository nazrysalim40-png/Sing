package com.example.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SongWriterSplit
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

/**
 * Data class representing track metadata collected by the form.
 */
data class TrackMetadataFormData(
    val title: String,
    val isrcCode: String,
    val releaseDateMillis: Long,
    val songwriterCredits: List<SongWriterSplit>
)

/**
 * Reusable Form Component to collect and manage essential track metadata:
 * 1. Song Title
 * 2. ISRC Code
 * 3. Songwriter Credits & Royalties
 * 4. Release Date
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrackMetadataForm(
    initialTitle: String = "",
    initialIsrc: String = "",
    initialReleaseDateMillis: Long = System.currentTimeMillis() + (14L * 24 * 60 * 60 * 1000),
    initialSongwriters: List<SongWriterSplit> = emptyList(),
    onSave: (TrackMetadataFormData) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var title by remember(initialTitle) { mutableStateOf(initialTitle) }
    var isrcCode by remember(initialIsrc) { mutableStateOf(initialIsrc) }
    var releaseDateMillis by remember(initialReleaseDateMillis) { mutableLongStateOf(initialReleaseDateMillis) }
    val songwriterList = remember(initialSongwriters) {
        mutableStateListOf<SongWriterSplit>().apply { addAll(initialSongwriters) }
    }

    // New contributor input state
    var showAddWriterSection by remember { mutableStateOf(false) }
    var newWriterName by remember { mutableStateOf("") }
    var newWriterRole by remember { mutableStateOf("Songwriter / Composer") }
    var newWriterPercentageText by remember { mutableStateOf("50.0") }
    var newWriterPro by remember { mutableStateOf("ASCAP") }
    var newWriterIpi by remember { mutableStateOf("") }

    // Validation and Computations
    val totalSplit = songwriterList.sumOf { it.percentage }
    val isSplit100 = Math.abs(totalSplit - 100.0) < 0.01
    val isTitleValid = title.isNotBlank()
    val isIsrcValid = isrcCode.isBlank() || isrcCode.matches(Regex("^[A-Z]{2}-?[A-Z0-9]{3}-?[0-9]{2}-?[0-9]{5}$", RegexOption.IGNORE_CASE))

    val dateFormatter = remember { SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault()) }
    val daysUntilRelease = remember(releaseDateMillis) {
        val diff = releaseDateMillis - System.currentTimeMillis()
        if (diff > 0) diff / (1000 * 60 * 60 * 24) else 0
    }

    // DatePicker Dialog launcher
    val calendar = remember(releaseDateMillis) {
        Calendar.getInstance().apply { timeInMillis = releaseDateMillis }
    }
    val datePickerDialog = remember(context, releaseDateMillis) {
        DatePickerDialog(
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
                releaseDateMillis = selectedCal.timeInMillis
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - (1000 * 60 * 60 * 24)
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = BorderStroke(1.dp, StudioBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("track_metadata_form")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Form Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElectricViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = ElectricViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Track Metadata Form",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Core release & distribution identifiers",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isTitleValid && isSplit100) MintGreen.copy(alpha = 0.15f) else AmberWarning.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, if (isTitleValid && isSplit100) MintGreen.copy(alpha = 0.4f) else AmberWarning.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isTitleValid && isSplit100) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (isTitleValid && isSplit100) MintGreen else AmberWarning,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isTitleValid && isSplit100) "Ready to Drop" else "Incomplete",
                            color = if (isTitleValid && isSplit100) MintGreen else AmberWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Divider(color = StudioBorder, thickness = 1.dp)

            // 1. SONG TITLE FIELD
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "1. SONG TITLE",
                        color = HyperCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${title.length}/100",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { if (it.length <= 100) title = it },
                    label = { Text("Track Title (Official Name)") },
                    placeholder = { Text("e.g., Midnight Neon Echoes") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = if (isTitleValid) HyperCyan else TextMuted
                        )
                    },
                    trailingIcon = {
                        if (title.isNotEmpty()) {
                            IconButton(onClick = { title = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    isError = title.isBlank(),
                    supportingText = {
                        if (title.isBlank()) {
                            Text("Song title is required for distribution", color = AmberWarning)
                        } else {
                            Text("Matches artist store label exactly", color = TextMuted)
                        }
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
                        unfocusedContainerColor = StudioBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_song_title")
                )
            }

            // 2. ISRC CODE FIELD
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "2. ISRC CODE",
                            color = HyperCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(12-char global track ID)",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    // Auto-generate sample ISRC button
                    Text(
                        text = "Generate Sample",
                        color = ElectricViolet,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable {
                                val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
                                val randomNum = (10000..99999).random()
                                isrcCode = "US-S1Z-$currentYear-$randomNum"
                            }
                            .testTag("button_generate_isrc")
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = isrcCode,
                    onValueChange = { isrcCode = it.uppercase() },
                    label = { Text("ISRC (e.g. US-S1Z-26-00001)") },
                    placeholder = { Text("CC-XXX-YY-NNNNN") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.QrCode,
                            contentDescription = null,
                            tint = if (isrcCode.isNotBlank() && isIsrcValid) MintGreen else TextMuted
                        )
                    },
                    trailingIcon = {
                        if (isrcCode.isNotBlank()) {
                            Icon(
                                imageVector = if (isIsrcValid) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isIsrcValid) MintGreen else AmberWarning,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    isError = !isIsrcValid,
                    supportingText = {
                        if (!isIsrcValid) {
                            Text("Standard format: 2-letter country code + 3-char registrant + 2-digit year + 5-digit ID", color = AmberWarning)
                        } else {
                            Text("Assigned by distributor or IFPI national agency", color = TextMuted)
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Next
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricViolet,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = StudioSurfaceHover,
                        unfocusedContainerColor = StudioBackground
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_isrc_code")
                )
            }

            // 3. RELEASE DATE FIELD & PRESETS
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3. TARGET RELEASE DATE",
                        color = HyperCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (daysUntilRelease > 0) "In $daysUntilRelease days" else "Live Today",
                        color = if (daysUntilRelease >= 14) MintGreen else AmberWarning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Release date trigger box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioSurfaceHover,
                    border = BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                        .testTag("input_release_date")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = ElectricViolet,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = dateFormatter.format(Date(releaseDateMillis)),
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Tap to choose release day (Distributors recommend Friday drops)",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit date",
                            tint = HyperCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Quick Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val presets = listOf(
                        "+1 Wk" to (7L * 24 * 60 * 60 * 1000),
                        "+2 Wks" to (14L * 24 * 60 * 60 * 1000),
                        "+1 Mo" to (30L * 24 * 60 * 60 * 1000),
                        "+2 Mos" to (60L * 24 * 60 * 60 * 1000)
                    )

                    presets.forEach { (label, offsetMillis) ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = StudioBackground,
                            border = BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    releaseDateMillis = System.currentTimeMillis() + offsetMillis
                                }
                        ) {
                            Box(
                                modifier = Modifier.padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // 4. SONGWRITER CREDITS & SPLITS
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "4. SONGWRITER CREDITS",
                            color = HyperCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${songwriterList.size} added)",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "${"%.1f".format(totalSplit)}% / 100%",
                        color = if (isSplit100) MintGreen else AmberWarning,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress indicator for splits
                LinearProgressIndicator(
                    progress = { (totalSplit / 100.0).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isSplit100) MintGreen else AmberWarning,
                    trackColor = StudioSurfaceHover,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(10.dp))

                // List of Songwriters
                if (songwriterList.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = StudioBackground,
                        border = BorderStroke(1.dp, StudioBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No songwriter credits entered yet",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Add primary writers, composers, and lyricists to legally protect publishing rights",
                                color = TextMuted,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        songwriterList.forEachIndexed { index, split ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StudioSurfaceHover,
                                border = BorderStroke(1.dp, StudioBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(ElectricViolet.copy(alpha = 0.2f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Person,
                                                contentDescription = null,
                                                tint = ElectricViolet,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = split.contributorName,
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${split.role} • ${split.proAffiliation} ${if (split.ipiNumber.isNotBlank()) "(IPI: ${split.ipiNumber})" else ""}",
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = HyperCyan.copy(alpha = 0.15f),
                                            border = BorderStroke(1.dp, HyperCyan.copy(alpha = 0.3f))
                                        ) {
                                            Text(
                                                text = "${"%.1f".format(split.percentage)}%",
                                                color = HyperCyan,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(4.dp))

                                        IconButton(
                                            onClick = { songwriterList.removeAt(index) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove writer",
                                                tint = NeonPink,
                                                modifier = Modifier.size(15.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Add Songwriter toggle or sub-form
                if (!showAddWriterSection) {
                    OutlinedButton(
                        onClick = {
                            val remaining = (100.0 - totalSplit).coerceAtLeast(0.0)
                            newWriterPercentageText = if (remaining > 0) "%.1f".format(remaining) else "50.0"
                            showAddWriterSection = true
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricViolet),
                        border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_open_add_songwriter")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Songwriter / Publishing Credit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Inline add songwriter card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = StudioBackground,
                        border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Add Songwriter Info",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(
                                    onClick = { showAddWriterSection = false },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = newWriterName,
                                onValueChange = { newWriterName = it },
                                label = { Text("Legal Songwriter Full Name") },
                                placeholder = { Text("e.g. Alex Morgan") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricViolet,
                                    unfocusedBorderColor = StudioBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newWriterRole,
                                    onValueChange = { newWriterRole = it },
                                    label = { Text("Role") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricViolet,
                                        unfocusedBorderColor = StudioBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.weight(1.2f)
                                )

                                OutlinedTextField(
                                    value = newWriterPercentageText,
                                    onValueChange = { newWriterPercentageText = it },
                                    label = { Text("Split %") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ElectricViolet,
                                        unfocusedBorderColor = StudioBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier.weight(0.8f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newWriterPro,
                                    onValueChange = { newWriterPro = it },
                                    label = { Text("PRO (ASCAP/BMI/SESAC)") },
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
                                    value = newWriterIpi,
                                    onValueChange = { newWriterIpi = it },
                                    label = { Text("IPI / CAE # (Opt)") },
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
                            }

                            // Quick role preset chips
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                listOf("Lyricist", "Composer", "Topliner", "Producer", "Co-Writer").forEach { presetRole ->
                                    FilterChip(
                                        selected = newWriterRole == presetRole,
                                        onClick = { newWriterRole = presetRole },
                                        label = { Text(presetRole, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = ElectricViolet.copy(alpha = 0.25f),
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    val pct = newWriterPercentageText.toDoubleOrNull() ?: 0.0
                                    if (newWriterName.isNotBlank() && pct > 0.0) {
                                        songwriterList.add(
                                            SongWriterSplit(
                                                releaseId = 0,
                                                contributorName = newWriterName.trim(),
                                                role = newWriterRole.trim(),
                                                percentage = pct,
                                                proAffiliation = newWriterPro.trim(),
                                                ipiNumber = newWriterIpi.trim()
                                            )
                                        )
                                        newWriterName = ""
                                        val remaining = (100.0 - songwriterList.sumOf { it.percentage }).coerceAtLeast(0.0)
                                        newWriterPercentageText = if (remaining > 0) "%.1f".format(remaining) else "0.0"
                                        newWriterIpi = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add Contributor", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Divider(color = StudioBorder, thickness = 1.dp)

            // Submit / Save Track Metadata Action
            Button(
                onClick = {
                    if (isTitleValid) {
                        onSave(
                            TrackMetadataFormData(
                                title = title.trim(),
                                isrcCode = isrcCode.trim(),
                                releaseDateMillis = releaseDateMillis,
                                songwriterCredits = songwriterList.toList()
                            )
                        )
                    }
                },
                enabled = isTitleValid,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricViolet,
                    contentColor = Color.White,
                    disabledContainerColor = StudioSurfaceHover,
                    disabledContentColor = TextMuted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("button_save_track_metadata")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save Essential Metadata",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
