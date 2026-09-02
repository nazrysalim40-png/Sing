package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.ElectricVioletLight
import com.example.ui.theme.HyperCyan
import com.example.ui.theme.MintGreen
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioSurfaceCard
import com.example.ui.theme.StudioSurfaceHover
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

val CuratedMusicGenres = listOf(
    "Pop",
    "Electronic",
    "Synthwave",
    "Indie Rock",
    "Hip Hop / R&B",
    "Alternative",
    "Acoustic / Folk",
    "Lo-Fi",
    "EDM / Dance",
    "Hyperpop",
    "Rock",
    "Ambient",
    "R&B / Soul"
)

/**
 * Multi-select chip UI component allowing artists to categorize their music release
 * across one or multiple genre tags with interactive chip selection, custom genre entry,
 * and clear active state indicators.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreMultiSelectChips(
    selectedGenres: Set<String>,
    onGenresChanged: (Set<String>) -> Unit,
    modifier: Modifier = Modifier,
    availableGenres: List<String> = CuratedMusicGenres,
    title: String = "Release Genres & Tags",
    maxSelection: Int = 5,
    allowCustomGenre: Boolean = true
) {
    var showCustomInput by remember { mutableStateOf(false) }
    var customGenreText by remember { mutableStateOf("") }
    var customGenresList by remember { mutableStateOf<List<String>>(emptyList()) }

    val allDisplayGenres = remember(availableGenres, customGenresList) {
        val list = availableGenres.toMutableList()
        customGenresList.forEach { custom ->
            if (!list.contains(custom)) {
                list.add(custom)
            }
        }
        list
    }

    fun toggleGenre(genre: String) {
        val newSet = selectedGenres.toMutableSet()
        if (newSet.contains(genre)) {
            newSet.remove(genre)
        } else {
            if (newSet.size < maxSelection) {
                newSet.add(genre)
            }
        }
        onGenresChanged(newSet)
    }

    fun addCustomGenre() {
        val trimmed = customGenreText.trim()
        if (trimmed.isNotBlank()) {
            if (!customGenresList.contains(trimmed)) {
                customGenresList = customGenresList + trimmed
            }
            toggleGenre(trimmed)
            customGenreText = ""
            showCustomInput = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("genre_multi_select_container")
    ) {
        // Header with label and count indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Sell,
                    contentDescription = null,
                    tint = HyperCyan,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title.uppercase(),
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selectedGenres.isNotEmpty()) ElectricViolet.copy(alpha = 0.2f) else StudioSurfaceHover,
                border = BorderStroke(1.dp, if (selectedGenres.isNotEmpty()) ElectricVioletLight else StudioBorder)
            ) {
                Text(
                    text = "${selectedGenres.size} Selected (Max $maxSelection)",
                    color = if (selectedGenres.isNotEmpty()) HyperCyan else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Multi-select FlowRow of chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            allDisplayGenres.forEach { genreName ->
                val isSelected = selectedGenres.contains(genreName)
                val testTagSuffix = genreName.lowercase().replace(" ", "_").replace("/", "_")

                FilterChip(
                    selected = isSelected,
                    onClick = { toggleGenre(genreName) },
                    label = {
                        Text(
                            text = genreName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    leadingIcon = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = StudioSurfaceHover,
                        labelColor = TextSecondary,
                        selectedContainerColor = ElectricViolet.copy(alpha = 0.35f),
                        selectedLabelColor = HyperCyan,
                        selectedLeadingIconColor = HyperCyan
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = StudioBorder,
                        selectedBorderColor = HyperCyan,
                        borderWidth = 1.dp,
                        selectedBorderWidth = 1.5.dp,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("genre_chip_$testTagSuffix")
                )
            }

            // Button to trigger custom genre input
            if (allowCustomGenre && !showCustomInput) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = StudioSurfaceHover,
                    border = BorderStroke(1.dp, StudioBorder),
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showCustomInput = true }
                        .testTag("button_add_custom_genre")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add custom genre",
                            tint = HyperCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Custom...",
                            color = HyperCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Inline Custom Genre Input field
        AnimatedVisibility(visible = showCustomInput) {
            Column(modifier = Modifier.padding(top = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = customGenreText,
                        onValueChange = { customGenreText = it },
                        placeholder = { Text("e.g. Cyberpunk Synth, Bedroom Pop") },
                        label = { Text("Custom Genre Tag") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { addCustomGenre() }),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = HyperCyan,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = StudioSurfaceHover,
                            unfocusedContainerColor = StudioSurfaceHover
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_custom_genre")
                    )

                    IconButton(
                        onClick = { addCustomGenre() },
                        enabled = customGenreText.isNotBlank(),
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (customGenreText.isNotBlank()) ElectricViolet else StudioSurfaceHover)
                            .testTag("button_confirm_custom_genre")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Genre",
                            tint = if (customGenreText.isNotBlank()) Color.White else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            showCustomInput = false
                            customGenreText = ""
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioSurfaceHover)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancel",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
