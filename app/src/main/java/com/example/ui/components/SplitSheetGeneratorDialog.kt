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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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

/**
 * Split Sheet Generator Dialog providing complete workflow:
 * 1. Input collaborators, contribution percentages, and contact details (email, phone).
 * 2. Real-time balance calculations, equal split allocator, and organized list.
 * 3. Formatted legal split sheet agreement generator with Copy & Share actions.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SplitSheetGeneratorDialog(
    songTitle: String = "Untitled Song",
    artistName: String = "Artist",
    initialSplits: List<SongWriterSplit> = emptyList(),
    onSaveSplits: (List<SongWriterSplit>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Editor & List, 1: Agreement Preview

    val splitsList = remember(initialSplits) {
        mutableStateListOf<SongWriterSplit>().apply {
            if (initialSplits.isNotEmpty()) addAll(initialSplits)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = StudioBackground),
            border = BorderStroke(1.dp, StudioBorder),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("dialog_split_sheet_generator")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(HyperCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = HyperCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LEGAL & ROYALTIES",
                                color = HyperCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Split Sheet Generator",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$songTitle • $artistName",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("button_close_split_generator_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Navigation Tabs (Editor vs Agreement Document)
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = StudioSurfaceCard,
                    contentColor = HyperCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = HyperCyan,
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Collaborators (${splitsList.size})", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Article, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Agreement Document", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content View
                if (selectedTab == 0) {
                    // TAB 0: Interactive Form & Organized List
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        SongSplitSheetForm(
                            songTitle = songTitle,
                            artistName = artistName,
                            initialSplits = splitsList.toList(),
                            onSaveSplits = { updated ->
                                splitsList.clear()
                                splitsList.addAll(updated)
                                onSaveSplits(updated)
                                Toast.makeText(context, "Split sheet saved successfully!", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            onExportClicked = { selectedTab = 1 },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    // TAB 1: Formatted Legal Document Preview & Quick Share
                    val docText = remember(splitsList.toList(), songTitle, artistName) {
                        MetadataExportUtils.generateSplitSheetDocument(
                            songTitle = songTitle,
                            artistName = artistName,
                            splits = splitsList.toList()
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = StudioSurfaceCard,
                            border = BorderStroke(1.dp, StudioBorder),
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "GENERATED SPLIT SHEET AGREEMENT",
                                        color = HyperCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                MetadataExportUtils.copyToClipboard(context, "Split Sheet Document", docText)
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = HyperCyan),
                                            border = BorderStroke(1.dp, HyperCyan.copy(alpha = 0.5f)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("button_copy_split_agreement")
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Copy Text", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = {
                                                MetadataExportUtils.shareViaSystemSheet(
                                                    context = context,
                                                    content = docText,
                                                    subject = "$songTitle - Split Sheet Agreement"
                                                )
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("button_share_split_agreement")
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Divider(color = StudioBorder)
                                Spacer(modifier = Modifier.height(10.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState())
                                        .background(StudioBackground.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = docText,
                                        color = TextPrimary,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { selectedTab = 0 },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                                border = BorderStroke(1.dp, StudioBorder),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Back to Editor", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    onSaveSplits(splitsList.toList())
                                    Toast.makeText(context, "Split sheet saved successfully!", Toast.LENGTH_SHORT).show()
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = HyperCyan, contentColor = StudioBackground),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Done & Save", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
