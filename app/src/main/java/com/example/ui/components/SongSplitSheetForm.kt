package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
import com.example.util.MetadataExportUtils
import java.math.BigDecimal
import java.math.RoundingMode

// Palette for collaborator visual avatars & meter segments
val SplitColors = listOf(
    HyperCyan,
    ElectricViolet,
    MintGreen,
    NeonPink,
    Color(0xFFFFB300), // Amber Gold
    Color(0xFF00E5FF), // Aqua
    Color(0xFFFF5252), // Coral Red
    Color(0xFFB388FF)  // Soft Lavender
)

val PopularRoles = listOf(
    "Primary Artist",
    "Songwriter / Lyricist",
    "Composer / Melody",
    "Producer / Beatmaker",
    "Featured Artist",
    "Mixing / Audio Engineer",
    "Publisher / Entity"
)

val PopularPros = listOf(
    "ASCAP",
    "BMI",
    "SESAC",
    "PRS for Music",
    "SOCAN",
    "GEMA",
    "SACEM",
    "Self-Published / None"
)

/**
 * Specialized Split Sheet Generator and Entry Form.
 * Allows users to input collaborators, specify contribution percentages, contact details
 * (email, phone number), and PRO/publishing details, with live allocation tracking and export.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SongSplitSheetForm(
    songTitle: String = "Untitled Track",
    artistName: String = "Artist",
    initialSplits: List<SongWriterSplit> = emptyList(),
    onSaveSplits: (List<SongWriterSplit>) -> Unit,
    onExportClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val splitsList = remember(initialSplits) {
        mutableStateListOf<SongWriterSplit>().apply {
            if (initialSplits.isNotEmpty()) addAll(initialSplits)
        }
    }

    // Input form state
    var editingSplitIndex by remember { mutableStateOf<Int?>(null) }
    var collaboratorName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Songwriter / Lyricist") }
    var splitPercentageText by remember { mutableStateOf("50.0") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var proAffiliation by remember { mutableStateOf("ASCAP") }
    var ipiNumber by remember { mutableStateOf("") }
    var publisherName by remember { mutableStateOf("") }

    // Dialog preview state
    var showQuickDocPreview by remember { mutableStateOf(false) }

    // Validation & calculations
    val totalPercentage = splitsList.sumOf { it.percentage }
    val formattedTotal = BigDecimal(totalPercentage).setScale(1, RoundingMode.HALF_UP).toDouble()
    val isExact100 = Math.abs(totalPercentage - 100.0) < 0.05
    val isUnderAllocated = totalPercentage < 99.95
    val isOverAllocated = totalPercentage > 100.05
    val remainingPercentage = if (100.0 > totalPercentage) 100.0 - totalPercentage else 0.0

    val currentInputPct = splitPercentageText.toDoubleOrNull() ?: 0.0
    val isNameValid = collaboratorName.isNotBlank()
    val isPctValid = currentInputPct > 0.0 && currentInputPct <= 100.0

    fun resetInputForm() {
        editingSplitIndex = null
        collaboratorName = ""
        selectedRole = "Songwriter / Lyricist"
        splitPercentageText = if (remainingPercentage > 0) "%.1f".format(remainingPercentage) else "25.0"
        email = ""
        phone = ""
        proAffiliation = "ASCAP"
        ipiNumber = ""
        publisherName = ""
    }

    fun startEditingSplit(index: Int) {
        val target = splitsList.getOrNull(index) ?: return
        editingSplitIndex = index
        collaboratorName = target.contributorName
        selectedRole = target.role
        splitPercentageText = "%.1f".format(target.percentage)
        email = target.email
        phone = target.phone
        proAffiliation = target.proAffiliation.ifBlank { "ASCAP" }
        ipiNumber = target.ipiNumber
        publisherName = target.publisher
    }

    fun commitCollaborator() {
        if (!isNameValid || !isPctValid) return
        val newSplit = SongWriterSplit(
            contributorName = collaboratorName.trim(),
            role = selectedRole.trim(),
            percentage = currentInputPct,
            email = email.trim(),
            phone = phone.trim(),
            proAffiliation = proAffiliation.trim(),
            ipiNumber = ipiNumber.trim(),
            publisher = publisherName.trim()
        )

        if (editingSplitIndex != null && editingSplitIndex in splitsList.indices) {
            splitsList[editingSplitIndex!!] = newSplit
        } else {
            splitsList.add(newSplit)
        }
        resetInputForm()
    }

    fun splitEqually() {
        if (splitsList.isEmpty()) return
        val count = splitsList.size
        val basePct = BigDecimal(100.0 / count).setScale(2, RoundingMode.DOWN).toDouble()
        var remainder = BigDecimal(100.0 - (basePct * count)).setScale(2, RoundingMode.HALF_UP).toDouble()

        val updated = splitsList.mapIndexed { idx, item ->
            val extra = if (idx == 0) remainder else 0.0
            item.copy(percentage = BigDecimal(basePct + extra).setScale(2, RoundingMode.HALF_UP).toDouble())
        }
        splitsList.clear()
        splitsList.addAll(updated)
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
        border = BorderStroke(1.dp, if (isExact100) MintGreen.copy(alpha = 0.5f) else StudioBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElectricViolet.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = ElectricViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "COPYRIGHT & ROYALTIES",
                            color = HyperCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Split Sheet Generator",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { showQuickDocPreview = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HyperCyan),
                        border = BorderStroke(1.dp, HyperCyan.copy(alpha = 0.5f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("button_generate_split_sheet_doc")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Generate Split Sheet Document",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Doc View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    if (onExportClicked != null) {
                        OutlinedButton(
                            onClick = onExportClicked,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricViolet),
                            border = BorderStroke(1.dp, ElectricViolet.copy(alpha = 0.5f)),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("button_export_split_sheet")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Export Split Sheet",
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Total Allocation Status & Visual Progress Meter
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = when {
                    isExact100 -> MintGreen.copy(alpha = 0.12f)
                    isOverAllocated -> NeonPink.copy(alpha = 0.12f)
                    else -> AmberWarning.copy(alpha = 0.12f)
                },
                border = BorderStroke(
                    1.dp,
                    when {
                        isExact100 -> MintGreen.copy(alpha = 0.4f)
                        isOverAllocated -> NeonPink.copy(alpha = 0.5f)
                        else -> AmberWarning.copy(alpha = 0.4f)
                    }
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = when {
                                    isExact100 -> Icons.Default.CheckCircle
                                    isOverAllocated -> Icons.Default.Warning
                                    else -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = when {
                                    isExact100 -> MintGreen
                                    isOverAllocated -> NeonPink
                                    else -> AmberWarning
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = when {
                                    isExact100 -> "100.0% Perfect Allocation"
                                    isOverAllocated -> "Over-allocated: ${"%.1f".format(totalPercentage - 100.0)}% over 100%"
                                    else -> "Unassigned: ${"%.1f".format(remainingPercentage)}% remaining"
                                },
                                color = when {
                                    isExact100 -> MintGreen
                                    isOverAllocated -> NeonPink
                                    else -> AmberWarning
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Total percentage badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isExact100 -> MintGreen.copy(alpha = 0.2f)
                                isOverAllocated -> NeonPink.copy(alpha = 0.2f)
                                else -> AmberWarning.copy(alpha = 0.2f)
                            },
                            modifier = Modifier.testTag("badge_split_total_percentage")
                        ) {
                            Text(
                                text = "${"%.1f".format(formattedTotal)}% / 100%",
                                color = when {
                                    isExact100 -> MintGreen
                                    isOverAllocated -> NeonPink
                                    else -> AmberWarning
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Multi-segment Visual Meter Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(StudioSurfaceHover)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            if (splitsList.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .background(Color.White.copy(alpha = 0.05f))
                                )
                            } else {
                                val total = if (totalPercentage > 0) totalPercentage.coerceAtLeast(100.0) else 100.0
                                splitsList.forEachIndexed { index, split ->
                                    val weight = (split.percentage / total).toFloat().coerceIn(0.001f, 1f)
                                    val color = SplitColors[index % SplitColors.size]
                                    Box(
                                        modifier = Modifier
                                            .weight(weight)
                                            .height(10.dp)
                                            .background(color)
                                    )
                                }
                                if (totalPercentage < 100.0) {
                                    val unallocatedWeight = ((100.0 - totalPercentage) / total).toFloat().coerceIn(0.001f, 1f)
                                    Box(
                                        modifier = Modifier
                                            .weight(unallocatedWeight)
                                            .height(10.dp)
                                            .background(Color.White.copy(alpha = 0.08f))
                                    )
                                }
                            }
                        }
                    }

                    // Meter Legend Chips
                    if (splitsList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            splitsList.forEachIndexed { index, split ->
                                val color = SplitColors[index % SplitColors.size]
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(StudioSurfaceHover, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = "${split.contributorName}: ${"%.1f".format(split.percentage)}%",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Calculator Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (splitsList.size > 1) {
                    OutlinedButton(
                        onClick = { splitEqually() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HyperCyan),
                        border = BorderStroke(1.dp, HyperCyan.copy(alpha = 0.4f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("button_split_equally")
                    ) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Split Equally (${"%.1f".format(100.0 / splitsList.size)}% each)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (isUnderAllocated && remainingPercentage > 0 && splitsList.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            splitPercentageText = "%.1f".format(remainingPercentage)
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MintGreen),
                        border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.4f)),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("button_fill_remaining_split")
                    ) {
                        Icon(imageVector = Icons.Default.Percent, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Fill Remainder (${"%.1f".format(remainingPercentage)}%)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Organized Collaborators List
            Text(
                text = "COLLABORATORS & CONTRIBUTION BREAKDOWN (${splitsList.size})",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (splitsList.isEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioSurfaceHover,
                    border = BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No collaborators added yet",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Use the entry form below to add collaborators, contribution percentages, and contact details.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                splitsList.forEachIndexed { index, split ->
                    val color = SplitColors[index % SplitColors.size]
                    val isEditingThis = editingSplitIndex == index

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isEditingThis) ElectricViolet.copy(alpha = 0.15f) else StudioSurfaceHover
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isEditingThis) ElectricViolet else StudioBorder
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("card_collaborator_$index")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    // Initials Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(color.copy(alpha = 0.25f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val initials = split.contributorName
                                            .split(" ")
                                            .filter { it.isNotBlank() }
                                            .mapNotNull { it.firstOrNull() }
                                            .take(2)
                                            .joinToString("")
                                            .uppercase()
                                            .ifBlank { "C" }

                                        Text(
                                            text = initials,
                                            color = color,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = split.contributorName,
                                            color = TextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${split.role} • ${split.proAffiliation}${if (split.ipiNumber.isNotBlank()) " (IPI: ${split.ipiNumber})" else ""}",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                        if (split.publisher.isNotBlank()) {
                                            Text(
                                                text = "Pub: ${split.publisher}",
                                                color = TextMuted,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Split % Chip
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = color.copy(alpha = 0.2f),
                                        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
                                    ) {
                                        Text(
                                            text = "${"%.1f".format(split.percentage)}%",
                                            color = color,
                                            fontWeight = FontWeight.Black,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    // Quick Edit Button
                                    IconButton(
                                        onClick = {
                                            if (isEditingThis) resetInputForm() else startEditingSplit(index)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isEditingThis) Icons.Default.Close else Icons.Default.Edit,
                                            contentDescription = "Edit Collaborator",
                                            tint = if (isEditingThis) NeonPink else HyperCyan,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    // Delete Button
                                    IconButton(
                                        onClick = {
                                            if (editingSplitIndex == index) resetInputForm()
                                            splitsList.removeAt(index)
                                        },
                                        modifier = Modifier
                                            .size(32.dp)
                                            .testTag("button_delete_collaborator_$index")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete Collaborator",
                                            tint = NeonPink.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            // Contact Details Row (if provided)
                            if (split.hasContactDetails) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (split.email.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = StudioSurfaceCard,
                                            border = BorderStroke(1.dp, StudioBorder),
                                            modifier = Modifier.clickable {
                                                MetadataExportUtils.copyToClipboard(context, "Email", split.email)
                                            }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Email,
                                                    contentDescription = null,
                                                    tint = HyperCyan,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = split.email,
                                                    color = TextSecondary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }

                                    if (split.phone.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = StudioSurfaceCard,
                                            border = BorderStroke(1.dp, StudioBorder),
                                            modifier = Modifier.clickable {
                                                MetadataExportUtils.copyToClipboard(context, "Phone", split.phone)
                                            }
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Phone,
                                                    contentDescription = null,
                                                    tint = MintGreen,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = split.phone,
                                                    color = TextSecondary,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
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

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Data Entry Form (Add or Edit Collaborator)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = StudioSurfaceHover,
                border = BorderStroke(1.dp, if (editingSplitIndex != null) ElectricViolet else StudioBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (editingSplitIndex != null) "EDIT COLLABORATOR" else "ADD COLLABORATOR",
                            color = if (editingSplitIndex != null) ElectricViolet else HyperCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        if (editingSplitIndex != null) {
                            Text(
                                text = "Cancel Edit",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { resetInputForm() }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legal Name / Contributor Name Input
                    OutlinedTextField(
                        value = collaboratorName,
                        onValueChange = { collaboratorName = it },
                        label = { Text("Legal Full Name or Artist Alias *") },
                        placeholder = { Text("e.g., Alex Rivers / Nova Beats") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isNameValid) HyperCyan else TextSecondary,
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
                            focusedContainerColor = StudioSurfaceCard,
                            unfocusedContainerColor = StudioSurfaceCard
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_collaborator_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Role Selection Chips
                    Text(
                        text = "Collaborator Role",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PopularRoles.forEach { roleOption ->
                            val isSelected = selectedRole == roleOption
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedRole = roleOption },
                                label = { Text(roleOption, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = ElectricViolet,
                                    selectedLabelColor = Color.White,
                                    containerColor = StudioSurfaceCard,
                                    labelColor = TextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Copyright & Royalty Split Percentage Input & Steppers
                    Text(
                        text = "Contribution / Royalty Share (%) *",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = splitPercentageText,
                            onValueChange = { splitPercentageText = it },
                            placeholder = { Text("50.0") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Percent,
                                    contentDescription = null,
                                    tint = HyperCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioSurfaceCard,
                                unfocusedContainerColor = StudioSurfaceCard
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_copyright_percentage")
                        )

                        // Quick Steppers (-5%, -1%, +1%, +5%)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(-5.0, -1.0, 1.0, 5.0).forEach { delta ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = StudioSurfaceCard,
                                    border = BorderStroke(1.dp, StudioBorder),
                                    modifier = Modifier
                                        .clickable {
                                            val cur = splitPercentageText.toDoubleOrNull() ?: 0.0
                                            val updated = (cur + delta).coerceIn(0.0, 100.0)
                                            splitPercentageText = "%.1f".format(updated)
                                        }
                                ) {
                                    Text(
                                        text = if (delta > 0) "+${delta.toInt()}%" else "${delta.toInt()}%",
                                        color = HyperCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Split Percentage Quick Presets
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(50.0, 33.3, 25.0, 20.0, 10.0).forEach { preset ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = StudioSurfaceCard,
                                border = BorderStroke(1.dp, StudioBorder),
                                modifier = Modifier
                                    .clickable { splitPercentageText = preset.toString() }
                            ) {
                                Text(
                                    text = "${preset.toInt()}%",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (remainingPercentage > 0) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MintGreen.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, MintGreen.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clickable { splitPercentageText = "%.1f".format(remainingPercentage) }
                            ) {
                                Text(
                                    text = "Rem (${"%.1f".format(remainingPercentage)}%)",
                                    color = MintGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact Details Section
                    Text(
                        text = "CONTACT DETAILS (OPTIONAL)",
                        color = HyperCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email Address") },
                            placeholder = { Text("collab@artist.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(16.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioSurfaceCard,
                                unfocusedContainerColor = StudioSurfaceCard
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_collaborator_email")
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Phone Number") },
                            placeholder = { Text("+1 555-0199") },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = MintGreen, modifier = Modifier.size(16.dp))
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioSurfaceCard,
                                unfocusedContainerColor = StudioSurfaceCard
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_collaborator_phone")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Rights & Organization: PRO Affiliation & IPI Number
                    Text(
                        text = "RIGHTS & PUBLISHING (OPTIONAL)",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = proAffiliation,
                            onValueChange = { proAffiliation = it },
                            label = { Text("PRO Affiliation") },
                            placeholder = { Text("ASCAP / BMI") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioSurfaceCard,
                                unfocusedContainerColor = StudioSurfaceCard
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_pro_affiliation")
                        )

                        OutlinedTextField(
                            value = ipiNumber,
                            onValueChange = { ipiNumber = it },
                            label = { Text("IPI / CAE #") },
                            placeholder = { Text("00123456789") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HyperCyan,
                                unfocusedBorderColor = StudioBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = StudioSurfaceCard,
                                unfocusedContainerColor = StudioSurfaceCard
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_ipi_number")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Publisher Name / Admin
                    OutlinedTextField(
                        value = publisherName,
                        onValueChange = { publisherName = it },
                        label = { Text("Publishing Company / Administrator") },
                        placeholder = { Text("e.g. Sony Music Pub / Kobalt / Self-Published") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HyperCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = StudioSurfaceCard,
                            unfocusedContainerColor = StudioSurfaceCard
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_publisher_name")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Add / Update Collaborator Button
                    Button(
                        onClick = { commitCollaborator() },
                        enabled = isNameValid && isPctValid,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (editingSplitIndex != null) ElectricViolet else HyperCyan,
                            contentColor = if (editingSplitIndex != null) Color.White else StudioBackground,
                            disabledContainerColor = StudioSurfaceCard,
                            disabledContentColor = TextMuted
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("button_add_collaborator")
                    ) {
                        Icon(
                            imageVector = if (editingSplitIndex != null) Icons.Default.Save else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (editingSplitIndex != null) "Update Collaborator" else "Add Collaborator to Split Sheet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Save Split Sheet Commit Action
            Button(
                onClick = { onSaveSplits(splitsList.toList()) },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricViolet,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("button_save_split_sheet")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save & Apply Split Sheet (${splitsList.size} Collaborators)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }

    // Modal quick document preview dialog
    if (showQuickDocPreview) {
        val agreementDoc = remember(splitsList.toList(), songTitle, artistName) {
            MetadataExportUtils.generateSplitSheetDocument(
                songTitle = songTitle,
                artistName = artistName,
                splits = splitsList.toList()
            )
        }

        Dialog(
            onDismissRequest = { showQuickDocPreview = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = StudioSurfaceCard),
                border = BorderStroke(1.dp, StudioBorder),
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .padding(vertical = 24.dp)
                    .testTag("dialog_quick_doc_preview")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Article, contentDescription = null, tint = HyperCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Split Sheet Agreement", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }

                        IconButton(onClick = { showQuickDocPreview = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(320.dp)
                            .verticalScroll(rememberScrollState())
                            .background(StudioBackground, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = agreementDoc,
                            color = TextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                MetadataExportUtils.copyToClipboard(context, "Split Sheet Document", agreementDoc)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HyperCyan),
                            border = BorderStroke(1.dp, HyperCyan.copy(alpha = 0.5f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copy Agreement", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                MetadataExportUtils.shareViaSystemSheet(
                                    context = context,
                                    content = agreementDoc,
                                    subject = "$songTitle - Split Sheet Agreement"
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share via App", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
